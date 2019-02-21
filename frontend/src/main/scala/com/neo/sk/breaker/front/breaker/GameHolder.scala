package com.neo.sk.breaker.front.breaker

import java.util.concurrent.atomic.AtomicInteger

import com.neo.sk.breaker.front.utils.byteObject.MiddleBufferInJs
import com.neo.sk.breaker.front.utils.{JsFunc, Shortcut}
import com.neo.sk.breaker.shared.ptcl
import com.neo.sk.breaker.shared.ptcl.Constants.GameState
import com.neo.sk.breaker.shared.ptcl.breaker.BrickClient
import com.neo.sk.breaker.shared.ptcl.model.{Boundary, Point}
import com.neo.sk.breaker.shared.ptcl.protocol._
import com.neo.sk.breaker.shared.ptcl.protocol.BreakerEvent.{GameBackendEvent, GameStateUpdate, SendAddBricks, SendEmoji}
import mhtml.Var
import org.scalajs.dom
import org.scalajs.dom.Blob
import org.scalajs.dom.ext.{Color, KeyCode}
import org.scalajs.dom.html.{Canvas, Image}
import org.scalajs.dom.raw.{Event, FileReader, KeyboardEvent, MessageEvent, MouseEvent}

import scala.collection.mutable
import scala.scalajs.js.typedarray.ArrayBuffer
import scala.xml.Elem
import org.scalajs.dom

/**
  * Created by Jingyi on 2018/11/9
  */
class GameHolder(canvasName:String) {

  import io.circe._, io.circe.generic.auto.exportDecoder, io.circe.parser._, io.circe.syntax._


  private[this] val canvas = dom.document.getElementById(canvasName).asInstanceOf[Canvas]
  private[this] val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

  private[this] val bounds = Point(ptcl.model.Boundary.w,ptcl.model.Boundary.h)

  private[this] val canvasUnit = 10
  private[this] val canvasBoundary = ptcl.model.Point(dom.window.innerWidth.toFloat, dom.window.innerHeight.toFloat)

  private[this] val canvasBounds = canvasBoundary / canvasUnit

  private[this] var myId = -1L
  private[this] var myName = ""
  private[this] var firstCome = true

  private[this] var gameState = GameState.mainMenu

  private[this] val websocketClient = new WebSocketClient(wsConnectSuccess,wsConnectError,wsMessageHandler,wsConnectClose)
  private[this] var breakerSchemaImplOpt: Option[BreakerSchemaImplClient] = None


  canvas.width = canvasBoundary.x.toInt
  canvas.height = canvasBoundary.y.toInt


  private var gameLoopTimer: Int = 0
  private var gameLoopCnt: Int = 0

  private var nextFrameMenu = 0
  private var nextFrameSolo = 0
  private var nextFrameDouble = 0
  private var logicFrameTime = System.currentTimeMillis()

  //游戏启动
  def start(name:String):Unit = {
    myName = name
    canvas.focus()
    websocketClient.setup(name)
    switchState(GameState.mainMenu)
    gameLoopTimer = Shortcut.schedule(gameLoop,ptcl.model.Frame.millsAServerFrame)
  }

  def switchState(state: Int): Unit ={
    dom.window.cancelAnimationFrame(nextFrameMenu)
    dom.window.cancelAnimationFrame(nextFrameSolo)
    dom.window.cancelAnimationFrame(nextFrameDouble)

    gameState = state
    gameState match {
      case GameState.mainMenu =>
        breakerSchemaImplOpt = Some(new BreakerMenu(ctx, canvasBoundary))
      case GameState.soloPlay =>
        breakerSchemaImplOpt = Some(new BreakerSoloPlay(ctx, bounds))
      case GameState.doublePlay =>
        //FIXME 连接前判断是否已经连接
        breakerSchemaImplOpt = None
        websocketClient.sendTextMsg("link")
      case _ =>
    }
    addActionListenEvent()
    pageRender()
  }

  def pageRender(): Unit ={
    gameState match {
      case GameState.mainMenu =>
        dom.window.requestAnimationFrame(menuRender())
      case GameState.soloPlay =>
        dom.window.requestAnimationFrame(soloGameRender())
      case GameState.doublePlay =>
        dom.window.requestAnimationFrame(doubleGameRender())
      case _ =>
        0
    }
  }

  def addActionListenEvent():Unit = {
    gameState match {
      case GameState.mainMenu =>
        menuActionListenEvent()
      case GameState.soloPlay =>
        soloActionListenEvent()
      case GameState.doublePlay =>
        doubleActionListenEvent()
      case _ =>
    }
  }

  def menuRender():Double => Unit = {d =>
    val curTime = System.currentTimeMillis()
    val offsetTime = curTime - logicFrameTime
    breakerSchemaImplOpt.foreach(_.drawGameByTime(offsetTime))
    nextFrameMenu = dom.window.requestAnimationFrame(menuRender())
  }

  def soloGameRender():Double => Unit = {d =>
    val curTime = System.currentTimeMillis()
    val offsetTime = curTime - logicFrameTime
    breakerSchemaImplOpt.foreach(_.drawGameByTime(offsetTime))
    nextFrameSolo = dom.window.requestAnimationFrame(soloGameRender())
  }

  def doubleGameRender():Double => Unit = {d =>
    val curTime = System.currentTimeMillis()
    val offsetTime = curTime - logicFrameTime

    if(breakerSchemaImplOpt.isEmpty){
      ctx.save()
      ctx.clearRect(0, 0, canvas.width, canvas.height)
      ctx.font = "40px Comic Sans Ms"
      ctx.textAlign = "center"
      ctx.fillText("find player...", canvas.width / 2, canvas.height / 2)
      ctx.restore()
    }
    else breakerSchemaImplOpt.foreach(_.drawGameByTime(offsetTime))

    nextFrameDouble = dom.window.requestAnimationFrame(doubleGameRender())
  }

  def gameLoop(): Unit ={
    breakerSchemaImplOpt.foreach(_.logicUpdate())
    logicFrameTime = System.currentTimeMillis()

    breakerSchemaImplOpt.foreach {
      case breakerDoublePlay: BreakerDoublePlay =>
        websocketClient.sendByteMsg(breakerDoublePlay.getUserState)

        if(breakerDoublePlay.gameEnergy >= 9){
          websocketClient.sendByteMsg(SendAddBricks)
          breakerDoublePlay.preExecuteUserEvent(BreakerEvent.MinusScore(9))
        }

        if(breakerDoublePlay.bricks.exists(_.position.y >= 340)){
          websocketClient.sendByteMsg(BreakerEvent.SendGameOver)
        }
      case _ =>
    }

    gameLoopCnt += 1
  }

  private def wsConnectSuccess(e:Event) = {
    println(s"连接服务器成功")
    e
  }
  private def wsConnectError(e:Event) = {
    JsFunc.alert("网络连接失败，请重新刷新")
    e
  }
  private def wsConnectClose(e:Event) = {
    JsFunc.alert("网络连接失败，请重新刷新")
    e
  }

  private def wsMessageHandler(e:MessageEvent) = {
    import com.neo.sk.breaker.front.utils.byteObject.ByteObject._
    e.data match {
      case blobMsg:Blob =>
        val fr = new FileReader()
        fr.readAsArrayBuffer(blobMsg)
        fr.onloadend = { _: Event =>
          val buf = fr.result.asInstanceOf[ArrayBuffer]
          val middleDataInJs = new MiddleBufferInJs(buf)
          bytesDecode[GameBackendEvent](middleDataInJs) match {
            case Right(data) =>
              data match {
                case BreakerEvent.RoomGameState(states) =>
                  // FIXME myState有可能不存在
                  if(gameState == GameState.doublePlay && breakerSchemaImplOpt.isEmpty){
                    val myState: BreakerEvent.UserGameState = states.find(_.name == myName).get
                    val otherState: BreakerEvent.UserGameState = states.find(_.name != myName).get
                    breakerSchemaImplOpt = Some(BreakerDoublePlay(ctx, bounds, canvasBoundary, myName, otherState.name, myState.shield, myState.pearl, myState.bricks))
                    pageRender()
                  }

                case state: BreakerEvent.UserGameState =>
                  if(gameState == GameState.doublePlay){
                    breakerSchemaImplOpt.foreach(_.preExecuteUserEvent(GameStateUpdate(state.shield, state.pearl, state.bricks, state.energy)))
                  }

                case BreakerEvent.GameStop(msg) =>
                  if(gameState == GameState.doublePlay){
                    JsFunc.alert(s"$msg")
                    switchState(GameState.mainMenu)
                  }

                case BreakerEvent.GetEmoji(name, t) =>
                  if(gameState == GameState.doublePlay){
                    breakerSchemaImplOpt.foreach(_.instantExecuteUserEvent(BreakerEvent.ShowEmoji(name, t)))
                  }

                case BreakerEvent.GetAddBricks =>
                  if(gameState == GameState.doublePlay){
                    breakerSchemaImplOpt.foreach(_.preExecuteUserEvent(BreakerEvent.AddBricks))
                  }

                case BreakerEvent.GetShot =>
                  breakerSchemaImplOpt.foreach(_.preExecuteUserEvent(BreakerEvent.DrawShotGun))

                case BreakerEvent.GetGameOver(winner) =>
                  breakerSchemaImplOpt.foreach{
                    case breakerSchemaImpl: BreakerDoublePlay =>
                      //FIXME 为了显示需要，otherBricks多画了一行
                      val newBricks = (0 to 9).map{ cnt =>
                        new BrickClient(Point(cnt * 80, 0), 1)
                      }.toList
                      if(winner == myName){
                        breakerSchemaImpl.otherBricks.foreach{ brick =>
                          brick.position = Point(brick.position.x, brick.position.y + brick.height)
                        }
                        breakerSchemaImpl.otherBricks = newBricks ::: breakerSchemaImpl.otherBricks
                      }

                      breakerSchemaImpl.winnerOpt = Some(winner)

                    case _ =>

                  }

                case msg => println(s"接收到无效消息: $msg")
              }
            case Left(error) =>
              println(s"decode msg failed,error:${error.message}")
          }
        }
      case unknown =>
        println(s"recv unknown msg:$unknown")
    }
    e
  }

  def menuActionListenEvent(): Unit ={
    canvas.focus()
    canvas.onmousedown = {e: MouseEvent =>
      breakerSchemaImplOpt.foreach{ breakerSchemaImpl =>
        val state = breakerSchemaImpl.instantExecuteUserEvent(BreakerEvent.MouseClick(e.clientX, e.clientY))
        switchState(state)
      }
      e.preventDefault()
    }
    canvas.onkeypress = {e: KeyboardEvent =>
      e.preventDefault()
    }
  }

  def soloActionListenEvent(): Unit ={
    canvas.focus()
    canvas.onmousedown = { e:MouseEvent =>
      e.preventDefault()
    }
    canvas.onkeypress = { e:KeyboardEvent =>
      e.key match {
        case "a" =>
          breakerSchemaImplOpt.foreach(_.instantExecuteUserEvent(BreakerEvent.ShieldMove(-10)))
        case "d" =>
          breakerSchemaImplOpt.foreach(_.instantExecuteUserEvent(BreakerEvent.ShieldMove(10)))
        case _ =>
          println("unknown")
      }
      e.preventDefault()
    }
  }

  def doubleActionListenEvent(): Unit ={
    canvas.focus()
    canvas.oncontextmenu = _ => false //取消右键弹出行为
    canvas.onmousedown = { e:MouseEvent =>
      e.button match {
        case 0 =>
          breakerSchemaImplOpt.foreach(_.preExecuteUserEvent(BreakerEvent.PearlTrans(1)))
          //FIXME breakerSchemaImplOpt is none
          if(breakerSchemaImplOpt.get.asInstanceOf[BreakerDoublePlay].winnerOpt.nonEmpty){
            breakerSchemaImplOpt.get.asInstanceOf[BreakerDoublePlay].winnerOpt = None
            switchState(GameState.mainMenu)
          }
        case 2 =>
          breakerSchemaImplOpt.foreach(_.preExecuteUserEvent(BreakerEvent.PearlTrans(2)))
        case _ =>
          println("unknown")
      }
      e.preventDefault()
    }
    canvas.onkeypress = { e:KeyboardEvent =>
      e.key match {
        case "1" =>
          websocketClient.sendByteMsg(SendEmoji(1))
        case "2" =>
          websocketClient.sendByteMsg(SendEmoji(2))
        case "3" =>
          websocketClient.sendByteMsg(SendEmoji(3))
        case "4" =>
          websocketClient.sendByteMsg(SendEmoji(4))
        case "5" =>
          websocketClient.sendByteMsg(SendEmoji(5))
        case "6" =>
          websocketClient.sendByteMsg(SendEmoji(6))
        case "7" =>
          websocketClient.sendByteMsg(SendEmoji(7))
        case "8" =>
          websocketClient.sendByteMsg(SendEmoji(8))
        case "q" =>
          //FIXME 冗余写法
          if(breakerSchemaImplOpt.map(_.isInstanceOf[BreakerDoublePlay]).get)
            if(breakerSchemaImplOpt.get.asInstanceOf[BreakerDoublePlay].gameSkillValue >= 4)
              websocketClient.sendByteMsg(BreakerEvent.SendShot)
          breakerSchemaImplOpt.foreach(_.preExecuteUserEvent(BreakerEvent.ShotGun))
        case "e" =>

        case "a" =>
          breakerSchemaImplOpt.foreach(_.instantExecuteUserEvent(BreakerEvent.ShieldMove(1)))
        case "d" =>
          breakerSchemaImplOpt.foreach(_.instantExecuteUserEvent(BreakerEvent.ShieldMove(2)))
        case _ =>
          println("unknown")
      }
      e.preventDefault()
    }
  }

}
