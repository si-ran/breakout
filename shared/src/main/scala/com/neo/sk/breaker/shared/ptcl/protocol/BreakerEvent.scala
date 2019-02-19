package com.neo.sk.breaker.shared.ptcl.protocol

import com.neo.sk.breaker.shared.ptcl.model.Point

/**
  * User: XuSiRan
  * Date: 2019/2/13
  * Time: 17:08
  */
object BreakerEvent {

  trait GameEvent

  sealed trait GameFrontEvent extends GameEvent

  sealed trait GameBackendEvent extends GameEvent

  trait GamePlayEvent extends GameEvent

  final case object EmptyFrontEvent extends GameFrontEvent

  final case object RoomLink extends GameFrontEvent

  final case class ShieldMove(hPosition: Short) extends GameFrontEvent with GamePlayEvent

  final case class SendEmoji(t: Byte) extends GameFrontEvent

  final case object SendAddBricks extends GameFrontEvent

  final case object SendGameOver extends GameFrontEvent

  final case class UserGameState(name: String, shield: Point, pearl: Point, bricks: List[Point]) extends GameFrontEvent with GameBackendEvent

  //
  final case class RoomGameState(states: List[UserGameState]) extends GameBackendEvent

  final case class GameStop(msg: String) extends GameBackendEvent

  final case class GetEmoji(name: String, t: Byte) extends GameBackendEvent

  final case object GetAddBricks extends GameBackendEvent

  final case class GetGameOver(win: String) extends GameBackendEvent

  //
  final case class ShowEmoji(name: String, t: Byte) extends GamePlayEvent

  final case class PearlTrans(dir: Byte) extends GamePlayEvent

  final case object AddBricks extends GamePlayEvent

  final case class MinusScore(score: Int) extends GamePlayEvent

  final case class BreakerGameOver(winner: String) extends GamePlayEvent

  final case class MouseClick(x: Double, y: Double) extends GamePlayEvent

  final case class GameStateUpdate(shield: Point, pearl: Point, bricks: List[Point]) extends GamePlayEvent

  //
  sealed trait WsMsg extends GameBackendEvent

  final case object WsComplete extends WsMsg

  final case object WsFailure extends WsMsg

  final case class Wrap(ws: Array[Byte]) extends WsMsg

}
