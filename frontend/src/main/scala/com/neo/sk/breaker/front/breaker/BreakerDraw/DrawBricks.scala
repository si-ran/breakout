package com.neo.sk.breaker.front.breaker.BreakerDraw

import com.neo.sk.breaker.front.breaker.BreakerDoublePlay
import com.neo.sk.breaker.shared.ptcl.breaker.BrickClient
import com.neo.sk.breaker.shared.ptcl.model.Point
import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.html.Image

import scala.collection.immutable

/**
  * User: XuSiRan
  * Date: 2019/2/19
  * Time: 15:43
  */
trait DrawBricks { this: BreakerDoublePlay =>

  val brickImages: immutable.IndexedSeq[Image] = (0 to 5).map(_ => dom.document.createElement("img").asInstanceOf[html.Image])
  (0 to 5).foreach(cnt => brickImages(cnt).setAttribute("src", s"/breakoutSIRAN/static/img/brick-${cnt + 1}.png"))

  def drawBricks(bricks: List[BrickClient], scale: Double, offsetPosition: Point): Unit ={
    bricks.foreach(brick =>
      ctx.drawImage(
        brickImages(brick.color),
        brick.position.x * scale + offsetPosition.x,
        brick.position.y * scale + offsetPosition.y,
        brick.width * scale,
        brick.height * scale
      )
    )
  }

  def drawBricksBlow(blowBricks: List[BlowBrick], scale: Double, offsetPosition: Point): Unit ={
    blowBricks.foreach{ blowBrick =>
      val sStartX = if(blowBrick.speed.x > 0) 32 else 0
      val StartX = if(blowBrick.speed.x > 0) (blowBrick.brick.width / 2) * scale else 0
      ctx.save()
      ctx.globalAlpha = 1 * blowBrick.time / 50.0
      ctx.drawImage(
        brickImages(blowBrick.brick.color),
        sStartX,
        0,
        (blowBrick.brick.width / 2) * scale,
        blowBrick.brick.height * scale,
        blowBrick.brick.position.x * scale + offsetPosition.x + StartX,
        blowBrick.brick.position.y * scale + offsetPosition.y,
        (blowBrick.brick.width / 2) * scale,
        blowBrick.brick.height * scale
      )
      ctx.restore()
      blowBrick.brick.position = blowBrick.brick.position + blowBrick.speed
      blowBrick.speed = Point(blowBrick.speed.x, blowBrick.speed.y + 0.15f)
      //FIXME 这里直接用了this的blowBrickList
      if(blowBrick.time < 1) blowBrickList = blowBrickList.filterNot(_.brick == blowBrick.brick)
      else blowBrick.time -= 1
    }
  }

}
