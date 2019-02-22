package com.neo.sk.breaker.shared.ptcl

/**
  * User: XuSiRan
  * Date: 2019/2/18
  * Time: 12:23
  */
package object protocol {

  trait CommonReq

  trait CommonRsp {
    val errCode: Int
    val msg: String
  }

  final case class ErrorRsp(
    errCode: Int,
    msg: String
  ) extends CommonRsp

  final case class SuccessRsp(
    errCode: Int = 0,
    msg: String = "ok"
  ) extends CommonRsp

  case class ComRsp(
    errCode: Int,
    msg: String
  ) extends CommonRsp

}
