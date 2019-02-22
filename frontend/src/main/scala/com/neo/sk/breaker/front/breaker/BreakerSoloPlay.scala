package com.neo.sk.breaker.front.breaker

import com.neo.sk.breaker.front.breaker.BreakerDraw.{DrawBricks, DrawOthers, DrawPearl, DrawShield}
import com.neo.sk.breaker.shared.ptcl.Constants.GameState
import com.neo.sk.breaker.shared.ptcl.breaker._
import com.neo.sk.breaker.shared.ptcl.component.RectangleOfBreaker
import com.neo.sk.breaker.shared.ptcl.model
import com.neo.sk.breaker.shared.ptcl.model.Point
import com.neo.sk.breaker.shared.ptcl.protocol.BreakerEvent._
import org.scalajs.dom

/**
  * User: XuSiRan
  * Date: 2019/1/29
  * Time: 18:25
  */
class BreakerSoloPlay(
  ctx: dom.CanvasRenderingContext2D,
  boundary: Point
) extends BreakerSchemaImplClient {

  val shield = new ShieldClient(Point(0, 500))
  val pearl = new PearlClient(Point(0, 0))
  var bricks = (0 to 108).map{ cnt =>
    val cntFinal = Point(cnt % 12, (cnt - 1) / 12)
    new BrickClient(Point(85 * cntFinal.x, 35 * cntFinal.y + 10), 1)
  }

  override def drawGameByTime(offsetTime:Long): Unit ={
    ctx.save()
    ctx.beginPath()
    ctx.clearRect(0, 0, dom.window.innerWidth, dom.window.innerHeight)
    ctx.fillRect(shield.position.x, shield.position.y, shield.width, shield.height)

    //offsetTime偏移
    val offsetPoint = pearl.speed * offsetTime / 120.0f
    ctx.arc(pearl.position.x + offsetPoint.x, pearl.position.y + offsetPoint.y, pearl.radius, 0, math.Pi * 2)
    ctx.stroke()
    ctx.fill()

    bricks.foreach(brick => ctx.fillRect(brick.position.x, brick.position.y, brick.width, brick.height))
    ctx.restore()
  }

  override def logicUpdate(): Unit ={
    updatePearlPosition()

    updatePearlSpeed()
  }

  def updatePearlPosition(): Unit ={
    val prePosition = pearl.position + pearl.speed
    val finalX =
      if(prePosition.x > boundary.x) boundary.x
      else if(prePosition.x <= 0) 0
      else prePosition.x
    val finalY =
      if(prePosition.y > boundary.y) boundary.y
      else if(prePosition.y <= 0) 0
      else prePosition.y
    pearl.position = Point(finalX, finalY)
  }

  def pearlChangeSpeed(rect: RectangleOfBreaker): Unit ={
    pearl.collided(rect) match {
      case 0 => pearl.speed = Point(pearl.speed.x, pearl.speed.y)
      case 1 => pearl.speed = Point(pearl.speed.x, -pearl.speed.y)
      case 2 => pearl.speed = Point(pearl.speed.x, -pearl.speed.y)
      case 3 => pearl.speed = Point(-pearl.speed.x, pearl.speed.y)
      case 4 => pearl.speed = Point(-pearl.speed.x, pearl.speed.y)
      case _ => pearl.speed = Point(pearl.speed.x, pearl.speed.y)
    }
  }

  def updatePearlSpeed(): Unit ={
    pearlChangeSpeed(shield)
    bricks.foreach{ brick =>
      pearlChangeSpeed(brick)
    }

    if(pearl.position.x <= 0) pearl.speed = Point(-pearl.speed.x, pearl.speed.y)
    if(pearl.position.x >= boundary.x) pearl.speed = Point(-pearl.speed.x, pearl.speed.y)
    if(pearl.position.y <= 0) pearl.speed = Point(pearl.speed.x, -pearl.speed.y)
    if(pearl.position.y >= boundary.y) pearl.speed = Point(pearl.speed.x, -pearl.speed.y)
  }

  override def preExecuteUserEvent(event: GamePlayEvent): Unit = {}

  override def instantExecuteUserEvent(event: GamePlayEvent): Int = {
    event match {
      case ShieldMove(moveX) =>
        if(moveX > 0) shield.changePosition(1)
        else shield.changePosition(2)
        GameState.soloPlay

      case _ =>
        GameState.soloPlay
    }
  }

}
