package com.neo.sk.breaker.http

import akka.actor.Scheduler
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.actor.typed.scaladsl.AskPattern._
import akka.stream.scaladsl.Flow
import akka.util.Timeout
import org.slf4j.LoggerFactory
import com.neo.sk.breaker.Boot.{userManager, executor}
import com.neo.sk.breaker.core.UserManager

import scala.concurrent.Future
import scala.language.postfixOps

/**
  * User: XuSiRan
  * Date: 2019/2/12
  * Time: 21:53
  */
trait WebSocketService extends ServiceUtils {

  import io.circe._
  import io.circe.generic.auto._

  implicit val timeout: Timeout

  implicit val scheduler: Scheduler

  private val log = LoggerFactory.getLogger(getClass)

  private val gameStart: Route = path("gameJoin"){
    parameter(
      'name.as[String]
    ) { name =>
      val flowFuture:Future[Flow[Message,Message,Any]] = userManager ? (UserManager.GameJoin(name, _))
      dealFutureResult(
        flowFuture.map(t => handleWebSocketMessages(t))
      )
    }
  }

  val wsRoutes: Route = gameStart


}
