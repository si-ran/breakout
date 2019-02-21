package com.neo.sk.breaker.core

import akka.actor.typed.ActorRef
import com.neo.sk.breaker.protocol.BreakerRoomProtocol.RoomUserInfo
import com.neo.sk.breaker.shared.ptcl.breaker.{BrickClient, PearlClient, ShieldClient}
import com.neo.sk.breaker.shared.ptcl.model.Point
import com.neo.sk.breaker.shared.ptcl.protocol.BreakerEvent.UserGameState

import scala.collection.{immutable, mutable}

/**
  * User: XuSiRan
  * Date: 2019/2/17
  * Time: 13:40
  */
class BreakerServerClient(
  user1: RoomUserInfo,
  user2: RoomUserInfo
) {

  val shieldMap: mutable.HashMap[String, ShieldClient] = mutable.HashMap(user1.name -> new ShieldClient(Point(20, 440)), user2.name -> new ShieldClient(Point(20, 440))) //name -> Shield
  val pearlMap: mutable.HashMap[String, PearlClient] = mutable.HashMap(user1.name -> new PearlClient(Point(30, 410)), user2.name -> new PearlClient(Point(30, 410))) //name -> PearlShield
  val bricksMap: mutable.HashMap[String, List[BrickClient]] = mutable.HashMap.empty[String, List[BrickClient]]
  val energyMap: mutable.HashMap[String, Int] = mutable.HashMap(user1.name -> 0, user2.name -> 0)

  var bricks: immutable.IndexedSeq[BrickClient] = (1 to 50).map{ cnt =>
    val cntFinal = Point((cnt - 1) % 10, (cnt - 1) / 10)
    new BrickClient(Point(80 * cntFinal.x, 40 * cntFinal.y), 1)
  }

  bricksMap.put(user1.name, bricks.toList)
  bricksMap.put(user2.name, bricks.toList)

  def getUserState(name: String): UserGameState ={
    UserGameState(
      name,
      shieldMap(name).position,
      pearlMap(name).position,
      bricksMap(name).map(_.position),
      energyMap(name)
    )
  }

  def changeState(state: UserGameState): Unit ={
    shieldMap(state.name).position = state.shield
    pearlMap(state.name).position = state.pearl
    bricksMap(state.name) = state.bricks.map(p => new BrickClient(p, 1))
    energyMap(state.name) = state.energy
  }

}
