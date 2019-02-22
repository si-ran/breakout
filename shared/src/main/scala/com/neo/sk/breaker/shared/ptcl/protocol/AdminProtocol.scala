package com.neo.sk.breaker.shared.ptcl.protocol

/**
  * User: XuSiRan
  * Date: 2019/2/18
  * Time: 12:23
  */
object AdminProtocol {

  case class BanUserReq(
    name: String
  ) extends CommonReq

  case class RoomStatics(
    id: Int,
    user1: String,
    user2: String
  )

  final case class GameStaticsRsp(
    roomStatics: List[RoomStatics],
    errCode: Int = 0,
    msg: String = "ok"
  ) extends CommonRsp

  case class OneUserStatic(
    name: String,
    win: Int,
    isBan: Boolean
  )

  final case class UserStaticsRsp(
    userStatics: List[OneUserStatic],
    errCode: Int = 0,
    msg: String = "ok"
  ) extends CommonRsp

}
