package com.neo.sk.breaker.front.breaker

import com.neo.sk.breaker.shared.ptcl.Constants.GameState
import com.neo.sk.breaker.shared.ptcl.breaker._
import com.neo.sk.breaker.shared.ptcl.model
import com.neo.sk.breaker.shared.ptcl.model.Point
import com.neo.sk.breaker.shared.ptcl.protocol.BreakerEvent._
import org.scalajs.dom
/**
  * User: XuSiRan
  * Date: 2019/2/14
  * Time: 18:13
  */
class BreakerMenu(
  ctx: dom.CanvasRenderingContext2D,
  canvasBoundary: Point
) extends BreakerSchemaImplClient {

  override def drawGameByTime(offsetTime:Long): Unit ={
    ctx.save()
    ctx.beginPath()

    ctx.clearRect(0, 0, canvasBoundary.x, canvasBoundary.y)

    ctx.font = "50px Wawati SC"
    ctx.textAlign = "center"
    ctx.fillText("打砖块", canvasBoundary.x / 2, canvasBoundary.y / 6)

    ctx.font = "30px Wawati SC"
    ctx.rect(canvasBoundary.x / 2 - 80, canvasBoundary.y / 3 - 40, 160, 60)
    ctx.stroke()
    ctx.fillText("单人游戏", canvasBoundary.x / 2, canvasBoundary.y / 3)

    ctx.rect(canvasBoundary.x / 2 - 80, canvasBoundary.y * 2 / 3 - 40, 160, 60)
    ctx.stroke()
    ctx.fillText("双人游戏", canvasBoundary.x / 2, canvasBoundary.y * 2 / 3)
    ctx.restore()
  }

  override def logicUpdate(): Unit = {}

  override def preExecuteUserEvent(event: GamePlayEvent): Unit = {}

  override def instantExecuteUserEvent(event: GamePlayEvent): Int = {
    event match {
      case MouseClick(x, y) =>
        if(x > canvasBoundary.x / 2 - 80 && x < canvasBoundary.x / 2 + 80 && y > canvasBoundary.y / 3 - 30 && y < canvasBoundary.y / 3 + 30) GameState.soloPlay
        else if(x > canvasBoundary.x / 2 - 80 && x < canvasBoundary.x / 2 + 80 && y > canvasBoundary.y * 2 / 3 - 30 && y < canvasBoundary.y * 2 / 3 + 30) GameState.doublePlay
        else GameState.mainMenu
      case _ =>
        GameState.mainMenu
    }
  }

}
