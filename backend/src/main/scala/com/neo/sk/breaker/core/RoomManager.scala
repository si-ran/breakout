package com.neo.sk.breaker.core

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, StashBuffer, TimerScheduler}
import com.neo.sk.breaker.protocol.BreakerRoomProtocol.RoomUserInfo
import com.neo.sk.breaker.shared.ptcl.protocol.AdminProtocol.{GameStaticsRsp, RoomStatics}
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration

/**
  * User: XuSiRan
  * Date: 2018/12/26
  * Time: 12:24
  */
object RoomManager {
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

  final case class UserJoinRoom(
    name: String,
    userActor: ActorRef[UserActor.Command]
  ) extends Command

  final case class UserDisConnect(
    name: String,
    userActor: ActorRef[UserActor.Command]
  ) extends Command

  final case class GameOver(roomId: Int) extends Command

  final case class GetStatics(replyTo: ActorRef[GameStaticsRsp]) extends Command

  private final case class ChildDead(
    id: Int,
    actor: ActorRef[RoomActor.Command]
  ) extends Command

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

  private def busy()(
    implicit stashBuffer: StashBuffer[Command],
    timer: TimerScheduler[Command]
  ): Behavior[Command] =
    Behaviors.receive[Command] { (ctx, msg) =>
      msg match {
        case SwitchBehavior(name, b, durationOpt, timeOut) =>
          switchBehavior(ctx, name, b, durationOpt, timeOut)

        case TimeOut(m) =>
          log.debug(s"${ctx.self.path} is time out when busy, msg=$m")
          switchBehavior(ctx, "init", init())

        case x =>
          stashBuffer.stash(x)
          Behavior.same

      }
    }

  def init(): Behavior[Command] =
    Behaviors.setup[Command] { ctx =>
      log.info(s"roomManager is starting...")
      implicit val stashBuffer: StashBuffer[Command] = StashBuffer[Command](Int.MaxValue)
      Behaviors.withTimers[Command] { implicit timer =>
        switchBehavior(ctx, "idle", idle(
          mutable.HashMap.empty[String, ActorRef[UserActor.Command]],
          mutable.HashMap.empty[Int, ActorRef[RoomActor.Command]],
          mutable.HashMap.empty[Int, (String, String)]
        ))
      }
    }

  private def idle(
    userMap: mutable.HashMap[String, ActorRef[UserActor.Command]], //name -> actor
    roomMap: mutable.HashMap[Int, ActorRef[RoomActor.Command]], //id -> actor
    userRoomMap: mutable.HashMap[Int, (String, String)] //id -> (name, name)

  )(
    implicit stashBuffer: StashBuffer[Command],
    timer: TimerScheduler[Command]
  ): Behavior[Command] ={
    Behaviors.receive[Command]{(ctx, msg) =>
      msg match {
        case GetStatics(replyTo) =>
          val rsp = GameStaticsRsp(userRoomMap.toList.map(t => RoomStatics(t._1, t._2._1, t._2._2)))
          replyTo ! rsp
          Behaviors.same

        case UserJoinRoom(name, replyToUserActor) =>
          if(userMap.contains(name)){
            Behaviors.same
          }
          else if(userMap.nonEmpty){
            val roomId = roomMap.keys.toArray.sorted.foldLeft(0)((a, b) => if(a + 1 >= b) b else a) + 1
            val roomActor = getRoomActor(ctx, roomId)
            val userActor = replyToUserActor
            val otherUser = userMap.head
            roomActor ! RoomActor.CreateRoom(RoomUserInfo(name, userActor), RoomUserInfo(otherUser._1, otherUser._2))
            userMap.remove(otherUser._1)
            roomMap.put(roomId, roomActor)
            userRoomMap.put(roomId, (name, otherUser._1))
            Behaviors.same
          }
          else
            userMap.put(name, replyToUserActor)
            Behaviors.same

        case UserDisConnect(name, replyTo) =>
          if(userMap.get(name).nonEmpty){
            userMap.remove(name)
          }
          if(userRoomMap.exists(userRoom => userRoom._2._1 == name || userRoom._2._2 == name)){
            val id = userRoomMap.find(userRoom => userRoom._2._1 == name || userRoom._2._2 == name).get._1
            val roomActor = roomMap(id)
            roomActor ! RoomActor.LinkOff(name)
            userRoomMap.remove(id)
            roomMap.remove(id)
          }
          Behaviors.same

        case GameOver(id) =>
          userRoomMap.remove(id)
          roomMap.remove(id)
          Behaviors.same

        case _ =>
          Behaviors.same
      }
    }
  }

  private def getRoomActor(ctx: ActorContext[Command], id: Int): ActorRef[RoomActor.Command] = {
    val childName = s"RoomActorActor-$id"
    ctx.child(childName).getOrElse {
      val actor = ctx.spawn(RoomActor.init(id), childName)
      ctx.watchWith(actor, ChildDead(id, actor))
      actor
    }.upcast[RoomActor.Command]
  }

}

