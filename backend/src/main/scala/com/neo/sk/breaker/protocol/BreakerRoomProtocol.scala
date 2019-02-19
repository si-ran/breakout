package com.neo.sk.breaker.protocol

import akka.actor.typed.ActorRef
import com.neo.sk.breaker.core.UserActor

/**
  * User: XuSiRan
  * Date: 2019/2/17
  * Time: 13:57
  */
object BreakerRoomProtocol {

  final case class RoomUserInfo(
    name: String,
    actor: ActorRef[UserActor.Command]
  )

}
