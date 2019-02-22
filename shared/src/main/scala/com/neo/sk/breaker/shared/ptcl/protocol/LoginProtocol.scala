package com.neo.sk.breaker.shared.ptcl.protocol

/**
  * User: XuSiRan
  * Date: 2019/2/17
  * Time: 23:55
  */
object LoginProtocol {

  final case class LoginReq(
    account: String,
    password: String
  ) extends CommonReq

  final case class SignUpReq(
    name: String,
    account: String,
    password: String
  ) extends CommonReq

  final case class LoginUserRsp(
    userName: String,
    errCode: Int = 0,
    msg: String = "ok"
  ) extends CommonRsp

}
