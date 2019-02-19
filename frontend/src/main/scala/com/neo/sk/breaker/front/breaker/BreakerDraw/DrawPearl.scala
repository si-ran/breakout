package com.neo.sk.breaker.front.breaker.BreakerDraw

import com.neo.sk.breaker.front.breaker.BreakerDoublePlay
import com.neo.sk.breaker.shared.ptcl.breaker.PearlClient
import com.neo.sk.breaker.shared.ptcl.model.Point

/**
  * User: XuSiRan
  * Date: 2019/2/19
  * Time: 11:12
  */
trait DrawPearl { this: BreakerDoublePlay =>

  def drawPearl(pearl: PearlClient, offsetTime: Long, scale: Double, offsetPosition: Point): Unit ={
    if(pearl.isShow){
      val offsetPoint =
        isPearlTrans match {
          case 1 =>
            (pearl.speed - Point(20, 0)) * offsetTime / 120.0f
          case 2 =>
            (pearl.speed + Point(20, 0)) * offsetTime / 120.0f
          case _ =>
            pearl.speed * offsetTime / 120.0f
        }
      ctx.beginPath()
      ctx.arc(
        (pearl.position.x + offsetPoint.x) * scale + offsetPosition.x,
        (pearl.position.y + offsetPoint.y) * scale + offsetPosition.y,
        pearl.radius * scale,
        0,
        math.Pi * 2)
      ctx.stroke()
      ctx.fill()
    }
    else{
      ctx.save()
      ctx.font = "35px Comic Sans Ms"
      ctx.fillStyle="#aa88aa"
      ctx.fillText(s"$pearlShowTime", boundary.x / 2 + offsetPosition.x, boundary.y / 2 + offsetPosition.y)
      ctx.restore()
    }
  }

}
