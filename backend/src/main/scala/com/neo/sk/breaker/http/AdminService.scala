package com.neo.sk.breaker.http

import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.neo.sk.breaker.models.dao.UserInfoDAO
import com.neo.sk.breaker.shared.ptcl.protocol.LoginProtocol.{LoginReq, LoginUserRsp}
import com.neo.sk.breaker.Boot.roomManager
import com.neo.sk.breaker.core.RoomManager
import com.neo.sk.breaker.shared.ptcl.protocol.AdminProtocol._
import com.neo.sk.breaker.shared.ptcl.protocol.{ErrorRsp, SuccessRsp}
import org.slf4j.LoggerFactory

import scala.concurrent.Future

/**
  * User: XuSiRan
  * Date: 2019/2/18
  * Time: 13:15
  */
trait AdminService extends ServiceUtils with BaseService {
  import io.circe._
  import io.circe.generic.auto._

  private val log = LoggerFactory.getLogger(getClass)

  private val getStatics: Route = (path("getStatics") & get){
    val getCodeOK: Future[GameStaticsRsp] = roomManager ? RoomManager.GetStatics
    dealFutureResult(
      getCodeOK.map( rsp =>
        complete(rsp)
      )
    )
  }

  private val getUserInfo : Route = (path("getUserInfo") & get){
    dealFutureResult(
      UserInfoDAO.allInfo().map{ userInfos =>
        complete(UserStaticsRsp(userInfos.map(t => OneUserStatic(t.userName, t.win, t.ban)).toList))
      }
    )
  }

  private val banUser: Route = (path("banUser") & post){
    entity(as[Either[Error, BanUserReq]]) {
      case Right(req) =>
        dealFutureResult(
          UserInfoDAO.getOneInfoByName(req.name).map{
            case Some((name, isBan)) =>
              if(isBan) UserInfoDAO.unBanUser(name)
              else UserInfoDAO.banUser(name)
              complete(SuccessRsp())
            case None => complete(ErrorRsp(30001, "此用户为游客"))
          }
        )
      case Left(e) =>
        complete(s"data parse error: $e")
    }
  }

  val adminRoutes: Route = pathPrefix("admin") {
    getStatics ~ getUserInfo ~ banUser
  }

}
