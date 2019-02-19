package com.neo.sk.breaker.shared.ptcl.protocol

/**
  * User: XuSiRan
  * Date: 2019/2/18
  * Time: 12:23
  */
object AdminProtocol {

  case class RoomStatics(
    id: Int,
    user1: String,
    user2: String
  )

  final case class GameStaticsRsp(
    roomStatics: List[RoomStatics],
    errCode: Int = 0,
    msg: String = "ok"
  )

}
