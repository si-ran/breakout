package com.neo.sk.breaker.http

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.neo.sk.breaker.common.AppSettings
import com.neo.sk.breaker.models.dao.UserInfoDAO
import com.neo.sk.breaker.shared.ptcl.protocol.{ErrorRsp, SuccessRsp}
import com.neo.sk.breaker.shared.ptcl.protocol.LoginProtocol._
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * User: XuSiRan
  * Date: 2019/2/18
  * Time: 0:11
  */
trait LoginService extends ServiceUtils with BaseService {
  import io.circe._
  import io.circe.generic.auto._

  private val log = LoggerFactory.getLogger(getClass)

  private val userLogin: Route = (path("userLogin") & post){
    entity(as[Either[Error, LoginReq]]) {
      case Right(req) =>
        dealFutureResult(
          UserInfoDAO.userLogin(req.account).map{
            case Some((name, password, isBan)) =>
              if(req.password == password && !isBan){
                complete(LoginUserRsp(name))
              }
              else if(isBan) complete(LoginUserRsp("error", 10003, "账号已禁用"))
              else complete(LoginUserRsp("error", 10002, "密码不正确"))
            case None =>
              complete(LoginUserRsp("error", 10001, "账号不存在"))
          }
        )

      case Left(e) =>
        complete(s"data parse error: $e")
    }
  }

  private val adminLogin: Route = (path("adminLogin") & post){
    entity(as[Either[Error, LoginReq]]) {
      case Right(req) =>
        req match {
          case LoginReq(AppSettings.adminAccount, AppSettings.adminPassword) =>
            complete(LoginUserRsp("admin"))
          case LoginReq(AppSettings.adminAccount, _) =>
            complete(LoginUserRsp("error", 20001, "password error"))
          case LoginReq(_, _) =>
            complete(LoginUserRsp("error", 20002, "account error"))
        }

      case Left(e) =>
        complete(s"data parse error: $e")
    }
  }

  private val signUp: Route = (path("signUp") & post){
    entity(as[Either[Error, SignUpReq]]) {
      case Right(req) =>
        dealFutureResult(
          UserInfoDAO.getOneInfo(req.account).flatMap{
            case Some(_) =>
              Future(complete(ErrorRsp(40001, "账号已存在")))
            case None =>
              UserInfoDAO.SignUp(req.name, req.account, req.password).map{
                case 0 => complete(ErrorRsp(40002, "注册失败"))
                case _ => complete(SuccessRsp())
              }
          }
        )

      case Left(e) =>
        complete(s"data parse error: $e")
    }
  }

  val loginRoutes: Route = pathPrefix("login") {
    userLogin ~ adminLogin ~ signUp
  }

}
