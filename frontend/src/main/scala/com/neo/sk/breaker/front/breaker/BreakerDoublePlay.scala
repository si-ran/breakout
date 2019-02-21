package com.neo.sk.breaker.front.breaker

import com.neo.sk.breaker.front.breaker.BreakerDraw.{DrawBricks, DrawOthers, DrawPearl, DrawShield}
import com.neo.sk.breaker.shared.ptcl.Constants.GameState
import com.neo.sk.breaker.shared.ptcl.breaker.{BrickClient, PearlClient, ShieldClient}
import com.neo.sk.breaker.shared.ptcl.component.RectangleOfBreaker
import com.neo.sk.breaker.shared.ptcl.model.Point
import com.neo.sk.breaker.shared.ptcl.model.random
import com.neo.sk.breaker.shared.ptcl.protocol.BreakerEvent
import com.neo.sk.breaker.shared.ptcl.protocol.BreakerEvent._
import org.scalajs.dom

/**
  * User: XuSiRan
  * Date: 2019/2/15
  * Time: 12:19
  */
case class BreakerDoublePlay(
  ctx: dom.CanvasRenderingContext2D,
  boundary: Point,
  canvasBoundary: Point,
  myName: String,
  otherName: String,
  shieldPosition: Point,
  pearlPosition: Point,
  bricksPosition: List[Point]
) extends BreakerSchemaImplClient
  with DrawShield
  with DrawPearl
  with DrawOthers
  with DrawBricks{

  val myWindowView = Point(260, 25)
  val otherWindowView = Point(70, 25)
  val otherWindowScale = 0.2

  var gameEnergy: Int = 0
  var otherGameEnergy: Int = 0
  var gameSkillValue: Int = 0
  var winnerOpt: Option[String] = None

  case class Emoji(
    user: String,
    t: Byte,
    var time: Int
  )
  var emojiList: List[Emoji] = Nil
  var pearlShowTime: Int = 0
  var isPearlTrans: Byte = 0 // pearl水平移动
  var isAddBricks: Boolean = false

  var shield = new ShieldClient(shieldPosition)
  var pearl = new PearlClient(pearlPosition)
  var bricks: List[BrickClient] = bricksPosition.map{ p => new BrickClient(p, (p.y / 40 % 8).toByte) }

  var otherShield = new ShieldClient(shieldPosition)
  var otherPearl = new PearlClient(pearlPosition)
  var otherBricks: List[BrickClient] = bricksPosition.map{ p => new BrickClient(p, 1) }

  var shotPearls: List[PearlClient] = Nil //散弹

  override def drawGameByTime(offsetTime:Long): Unit ={
    ctx.save()

    ctx.clearRect(0, 0, dom.window.innerWidth, dom.window.innerHeight)

    drawEmojiList()
    drawSkillValue()

    //FIXME 缩放
    ctx.save()
    ctx.beginPath()
    ctx.font = "15px arial"
    ctx.textAlign = "left"
    ctx.fillText(s"$otherName", otherWindowView.x, otherWindowView.y - 3)
    ctx.rect(otherWindowView.x, otherWindowView.y, boundary.x * otherWindowScale, boundary.y * otherWindowScale)
    ctx.stroke()
    ctx.restore()
    //红线
    ctx.save()
    ctx.beginPath()
    ctx.moveTo(otherWindowView.x, otherWindowView.y + boundary.y * otherWindowScale * 19 / 25)
    ctx.lineTo(otherWindowView.x + boundary.x  * otherWindowScale, otherWindowView.y + boundary.y  * otherWindowScale * 19 / 25)
    ctx.strokeStyle = "red"
    ctx.stroke()
    ctx.restore()

    drawEnergy(1, otherWindowView, otherWindowScale)

    drawBricks(otherBricks, otherWindowScale, otherWindowView)
    drawOneShield(otherShield, otherWindowScale, otherWindowView)
    drawPearl(otherPearl, 0, otherWindowScale, otherWindowView, isLast = false)

    ctx.save()
    ctx.beginPath()
    ctx.font = "21px arial"
    ctx.textAlign = "left"
    ctx.fillText(s"$myName", myWindowView.x, myWindowView.y - 3)
    ctx.rect(myWindowView.x, myWindowView.y, boundary.x, boundary.y)
    ctx.stroke()
    ctx.restore()
    //红线
    ctx.save()
    ctx.beginPath()
    ctx.moveTo(myWindowView.x, myWindowView.y + boundary.y * 19 / 25)
    ctx.lineTo(myWindowView.x + boundary.x, myWindowView.y + boundary.y * 19 / 25)
    ctx.strokeStyle = "red"
    ctx.stroke()
    ctx.restore()

    drawEnergy(gameEnergy, myWindowView, 1)

    drawBricks(bricks, 1, myWindowView)
    drawOneShield(shield, 1, myWindowView)
    drawPearl(pearl, offsetTime, 1, myWindowView, isLast = true)
    shotPearls.foreach(drawPearl(_, offsetTime, 1, myWindowView, isLast = false))

    ctx.restore()

    if(winnerOpt.nonEmpty){
      ctx.save()
      ctx.fillStyle = "rgba(0,0,0,0.5)"
      ctx.fillRect(0, 0, canvasBoundary.x, canvasBoundary.y)
      ctx.fillStyle = "#99ffed"
      ctx.font = "60px Comic Sans Ms"
      ctx.textAlign = "center"
      ctx.fillText(s"${winnerOpt.get} win!!", canvasBoundary.x / 2, canvasBoundary.y / 2)
      ctx.restore()
    }
  }

  override def logicUpdate(): Unit ={
    if(! pearl.isShow){
      pearlShowTime -= 1
      if(pearlShowTime < 1) pearl.isShow = true
    }
    else{
      updatePearlPosition()
      updatePearlSpeed()
    }

    updateBricks()

    emojiList = emojiList.filterNot(_.time <= 0)
    emojiList.foreach(_.time -= 1)
  }

  def updatePearlPosition(): Unit ={

    isPearlTrans match {
      case 1 =>
        pearl.position = pearl.position - Point(20, 0)
        isPearlTrans = 0
      case 2 =>
        pearl.position = pearl.position + Point(20, 0)
        isPearlTrans = 0
      case _ =>
    }
    pearl.position = pearl.position + pearl.speed
    shotPearls.foreach(p => p.position = p.position + p.speed)
  }

  def pearlChangeSpeed(rect: RectangleOfBreaker): Unit ={
    pearl.collided(rect) match {
      case 0 =>
        pearl.speed = Point(pearl.speed.x, pearl.speed.y)
      case 1 =>
        pearl.speed = Point(pearl.speed.x, -pearl.speed.y)
        if(rect.isInstanceOf[ShieldClient]) pearl.speed = Point(pearl.speed.x, -math.abs(pearl.speed.y))
      case 2 =>
        pearl.speed = Point(pearl.speed.x, -pearl.speed.y)
        if(rect.isInstanceOf[ShieldClient]) pearl.speed = Point(pearl.speed.x, -math.abs(pearl.speed.y))
      case 3 =>
        pearl.speed = Point(-pearl.speed.x, pearl.speed.y)
      case 4 =>
        pearl.speed = Point(-pearl.speed.x, pearl.speed.y)
      case _ =>
        pearl.speed = Point(pearl.speed.x, pearl.speed.y)
    }
  }

  def updatePearlSpeed(): Unit ={

    pearlChangeSpeed(shield)
    bricks.foreach{ brick =>
      if (pearl.collided(brick) != 0) {
        bricks = bricks.filterNot(t => t == brick)
        gameEnergy += 1
        gameSkillValue += 1
      }
      pearlChangeSpeed(brick)
    }
    //散弹判断
    shotPearls.foreach{ shotPearl =>
      var isCollied = false
      bricks.foreach{ brick =>
        if (shotPearl.collided(brick) != 0 && !isCollied) {
          bricks = bricks.filterNot(t => t == brick)
          gameEnergy += 1
          gameSkillValue += 1
          isCollied = true
          shotPearls = shotPearls.filterNot(_ == shotPearl)
        }
      }
    }


    if(pearl.position.x <= pearl.radius) {
      pearl.position = Point(pearl.radius, pearl.position.y)
      pearl.speed = Point(-pearl.speed.x, pearl.speed.y)
    }
    if(pearl.position.x >= boundary.x - pearl.radius){
      pearl.position = Point(boundary.x - pearl.radius, pearl.position.y)
      pearl.speed = Point(-pearl.speed.x, pearl.speed.y)
    }
    if(pearl.position.y <= pearl.radius){
      pearl.position = Point(pearl.position.x, pearl.radius)
      pearl.speed = Point(pearl.speed.x, -pearl.speed.y)
    }
    if(pearl.position.y >= boundary.y - pearl.radius) {
      pearl.isShow = false
      pearl.position = Point(10, 410)
      pearl.speed = Point(20, -20)
      pearlShowTime = 50
    }
  }

  def updateBricks(): Unit ={
    if(isAddBricks){
      val newColor = random.nextInt(5)
      bricks.foreach{ brick =>
        brick.position = Point(brick.position.x, brick.position.y + brick.height)
      }
      val newBricks = (0 to 9).map{ cnt =>
        new BrickClient(Point(cnt * 80, 0), newColor.toByte)
      }.toList
      bricks = newBricks ::: bricks
      isAddBricks = false
    }
  }

  override def preExecuteUserEvent(event: GamePlayEvent): Unit = {
    event match {
      case PearlTrans(dir) =>
        isPearlTrans = dir
      case AddBricks =>
        isAddBricks = true
      case MinusScore(score) =>
        gameEnergy -= score
      case ShotGun =>
        if (gameSkillValue >= 4){
          shotPearls = (0 to 2).toList.map{ _ =>
            new PearlClient(pearl.position, pearl.speed + Point(random.nextFloat() * pearl.speed.x, random.nextFloat() * pearl.speed.y))
          } ::: shotPearls
          gameSkillValue -= 4
        }
      case _ =>
    }
  }

  override def instantExecuteUserEvent(event: GamePlayEvent): Int = {
    event match {
      case otherState: GameStateUpdate =>
        otherShield.position = otherState.shield
        otherPearl.position = otherState.pearl
        otherBricks = otherState.bricks.map(p => new BrickClient(p, 1))
        GameState.remain
      case ShieldMove(dir) =>
        if(dir == 1 && shield.position.x > 1) shield.changePosition(1)
        else if(dir == 2 && shield.position.x + shield.width < boundary.x - 1) shield.changePosition(2)
        GameState.remain
      case ShowEmoji(userName, t) =>
        emojiList = Emoji(userName, t, 50) :: emojiList
        GameState.remain
      case _ =>
        GameState.remain
    }
  }

  def getUserState: BreakerEvent.UserGameState ={
    BreakerEvent.UserGameState(
      myName,
      shield.position,
      pearl.position,
      bricks.map(_.position)
    )
  }

}
