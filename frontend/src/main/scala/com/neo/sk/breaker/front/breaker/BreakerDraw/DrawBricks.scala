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
  (0 to 5).foreach(cnt => brickImages(cnt).setAttribute("src", s"/breaker/static/img/brick-${cnt + 1}.png"))

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

}
