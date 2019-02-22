package com.neo.sk.breaker.http

import akka.actor.{ActorSystem, Scheduler}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import akka.util.Timeout
import com.neo.sk.breaker.common.AppSettings
import akka.actor.typed.scaladsl.AskPattern._

import scala.concurrent.{ExecutionContextExecutor, Future}
import com.neo.sk.breaker.Boot.{executor, scheduler, timeout}


trait HttpService
  extends ResourceService
  with ServiceUtils
  with WebSocketService
  with LoginService
  with AdminService {

  import akka.actor.typed.scaladsl.AskPattern._
  import com.neo.sk.utils.CirceSupport._
  import io.circe.generic.auto._

  implicit val system: ActorSystem

  implicit val executor: ExecutionContextExecutor

  implicit val materializer: Materializer

  implicit val timeout: Timeout

  implicit val scheduler: Scheduler


  import akka.actor.typed.scaladsl.adapter._


  val routes: Route =
    ignoreTrailingSlash {
      pathPrefix("breakoutSIRAN") {
        pathEndOrSingleSlash {
          getFromResource("html/admin.html")
        } ~ resourceRoutes ~ wsRoutes ~ loginRoutes ~ adminRoutes
      }
    }

//  lazy val routes: Route = pathPrefix(AppSettings.rootPath) {
//    resourceRoutes ~
//      (pathPrefix("game") & get){
//        pathEndOrSingleSlash{
//          getFromResource("html/admin.html")
//        } ~
//          path("join"){
//          parameter('name){ name =>
//            log.debug(s"sssssssssname=${name}")
//            complete("sss")
//          }
//        }
//
//      }
//  }




}
