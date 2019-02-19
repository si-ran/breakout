package com.neo.sk.breaker.http

import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.neo.sk.breaker.models.dao.LoginDAO
import com.neo.sk.breaker.shared.ptcl.protocol.LoginProtocol.{LoginReq, LoginUserRsp}
import com.neo.sk.breaker.Boot.roomManager
import com.neo.sk.breaker.core.RoomManager
import com.neo.sk.breaker.shared.ptcl.protocol.AdminProtocol.{GameStaticsRsp, RoomStatics}
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

  val adminRoutes: Route = pathPrefix("admin") {
    getStatics
  }

}
