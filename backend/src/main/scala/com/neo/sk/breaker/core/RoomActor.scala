package com.neo.sk.breaker.core

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, StashBuffer, TimerScheduler}
import com.neo.sk.breaker.Boot.roomManager
import com.neo.sk.breaker.core.UserActor.{UserJoinRoomSuccess, WsBackMessage}
import com.neo.sk.breaker.protocol.BreakerRoomProtocol.RoomUserInfo
import com.neo.sk.breaker.shared.ptcl.protocol.BreakerEvent._
import org.slf4j.LoggerFactory

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration._

/**
  * User: XuSiRan
  * Date: 2018/12/26
  * Time: 12:24
  */
object RoomActor {
  private val log = LoggerFactory.getLogger(this.getClass)

  trait Command

  private case class TimeOut(msg: String) extends Command

  private final case class SwitchBehavior(
    name: String,
    behavior: Behavior[Command],
    durationOpt: Option[FiniteDuration] = None,
    timeOut: TimeOut = TimeOut("busy time error")
  ) extends Command

  private final case object BehaviorChangeKey
  private final case object GameUpdateKey

  final case object GameLoop extends Command

  final case class CreateRoom(user1: RoomUserInfo, user2: RoomUserInfo) extends Command

  final case class LinkOff(offName: String) extends Command

  final case class UpdateStateFromFront(state: UserGameState) extends Command

  final case class ShowEmoji(name: String, t: Byte) extends Command

  final case class AddBricks(name: String) extends Command

  final case class ShotGun(name: String) extends Command

  final case class OneSideWin(loser: String) extends Command

  private[this] def switchBehavior(
    ctx: ActorContext[Command],
    behaviorName: String,
    behavior: Behavior[Command],
    durationOpt: Option[FiniteDuration] = None,
    timeOut: TimeOut = TimeOut("busy time error"))
    (implicit stashBuffer: StashBuffer[Command],
      timer: TimerScheduler[Command]): Behavior[Command] = {
    timer.cancel(BehaviorChangeKey)
    durationOpt.foreach(timer.startSingleTimer(BehaviorChangeKey, timeOut, _))
    stashBuffer.unstashAll(ctx, behavior)
  }

  private def busy(id: Int)(
    implicit stashBuffer: StashBuffer[Command],
    timer: TimerScheduler[Command]
  ): Behavior[Command] =
    Behaviors.receive[Command] { (ctx, msg) =>
      msg match {
        case SwitchBehavior(name, b, durationOpt, timeOut) =>
          switchBehavior(ctx, name, b, durationOpt, timeOut)

        case TimeOut(m) =>
          log.debug(s"${ctx.self.path} is time out when busy, msg=$m")
          switchBehavior(ctx, "init", init(id))

        case x =>
          stashBuffer.stash(x)
          Behavior.same

      }
    }

  def init(id: Int): Behavior[Command] =
    Behaviors.setup[Command] { ctx =>
      log.info(s"roomActor($id) is starting...")
      implicit val stashBuffer: StashBuffer[Command] = StashBuffer[Command](Int.MaxValue)
      Behaviors.withTimers[Command] { implicit timer =>
        Behaviors.receiveMessage {
          case CreateRoom(user1, user2) =>
            val roomClient = new BreakerServerClient(user1, user2)
            user1.actor ! UserJoinRoomSuccess(ctx.self, RoomGameState(List(roomClient.getUserState(user1.name), roomClient.getUserState(user2.name))))
            user2.actor ! UserJoinRoomSuccess(ctx.self, RoomGameState(List(roomClient.getUserState(user1.name), roomClient.getUserState(user2.name))))
            timer.startSingleTimer(GameUpdateKey, GameLoop, 1.seconds)
            switchBehavior(ctx, "idle", idle(id, roomClient, user1, user2))

          case _ =>
            Behaviors.same
        }
      }
    }

  private def idle(
    id: Int,
    roomClient: BreakerServerClient,
    user1: RoomUserInfo,
    user2: RoomUserInfo
  )(
    implicit stashBuffer: StashBuffer[Command],
    timer: TimerScheduler[Command]
  ): Behavior[Command] ={
    Behaviors.receive[Command]{(ctx, msg) =>
      msg match {
        case GameLoop =>
          user1.actor ! UserActor.UserAccord(roomClient.getUserState(user2.name))
          user2.actor ! UserActor.UserAccord(roomClient.getUserState(user1.name))
          timer.startSingleTimer(GameUpdateKey, GameLoop, 120.millis)
          Behaviors.same

        case LinkOff(name) =>
          if(user1.name == name){
            user2.actor ! UserActor.StopGame("other player link off")
          }
          else{
            user1.actor ! UserActor.StopGame("other player link off")
          }
          Behaviors.stopped

        case UpdateStateFromFront(state) =>
          roomClient.changeState(state)
          Behaviors.same

        case ShowEmoji(name, t) =>
          user1.actor ! WsBackMessage(GetEmoji(name, t))
          user2.actor ! WsBackMessage(GetEmoji(name, t))
          Behaviors.same

        case ShotGun(name) =>
            if(user1.name != name){
              user1.actor ! WsBackMessage(GetShot)
            }
            else{
              user2.actor ! WsBackMessage(GetShot)
            }
          Behaviors.same

        case AddBricks(name) =>
          if(user1.name != name){
            user1.actor ! WsBackMessage(GetAddBricks)
          }
          else{
            user2.actor ! WsBackMessage(GetAddBricks)
          }
          Behaviors.same

        case OneSideWin(loser) =>
          val winner = if(user1.name ==loser) user2.name else user1.name
          roomManager ! RoomManager.GameOver(id)
          user1.actor ! WsBackMessage(GetGameOver(winner))
          user2.actor ! WsBackMessage(GetGameOver(winner))
          Behaviors.stopped

        case _ =>
          Behaviors.same
      }
    }
  }

}

