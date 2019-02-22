package com.neo.sk.breaker.front.breaker.BreakerDraw

import com.neo.sk.breaker.front.breaker.BreakerDoublePlay
import com.neo.sk.breaker.shared.ptcl.breaker.ShieldClient
import com.neo.sk.breaker.shared.ptcl.model.Point
import org.scalajs.dom
import org.scalajs.dom.html

/**
  * User: XuSiRan
  * Date: 2019/2/18
  * Time: 15:49
  */
trait DrawShield { this: BreakerDoublePlay =>

  val shieldImg = dom.document.createElement("img").asInstanceOf[html.Image]
  shieldImg.setAttribute("src", s"/breakoutSIRAN/static/img/shield.png")

  def drawOneShield(shield: ShieldClient, scale: Double, offsetPosition: Point): Unit ={
//    val finalPosition = shield.position * scale.toFloat + offsetPosition
//    val finalHeight = shield.height * scale
//    val finalWidth = shield.width * scale

    ctx.save()
    ctx.drawImage(
      shieldImg,
      shield.position.x * scale + offsetPosition.x,
      shield.position.y * scale + offsetPosition.y,
      shield.width * scale,
      shield.height * scale)
//    ctx.beginPath()
//    ctx.moveTo(finalPosition.x, finalPosition.y)
//    ctx.lineTo(finalPosition.x + finalWidth, finalPosition.y)
//    ctx.arc(finalPosition.x + finalWidth, finalPosition.y + finalHeight / 2, finalHeight / 2, - math.Pi / 2, math.Pi / 2)
//    ctx.lineTo(finalPosition.x, finalPosition.y + finalHeight)
//    ctx.arc(finalPosition.x, finalPosition.y + finalHeight / 2, finalHeight / 2, math.Pi / 2, - math.Pi / 2)
//    ctx.fill()
    ctx.restore()
  }

}
