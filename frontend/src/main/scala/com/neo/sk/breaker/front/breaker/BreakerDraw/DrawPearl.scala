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

  case class PearlTail(
    point: Point,
    var trans: Float,
  )
  var pearlPositionList: List[PearlTail] = Nil
  var isSavePearlPosition: Int = 1

  def drawPearl(pearl: PearlClient, offsetTime: Long, scale: Double, offsetPosition: Point, isLast: Boolean): Unit ={
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
      //甩尾
      if(isLast){ pearlPositionList.foreach{ point =>
        ctx.save()
        ctx.beginPath()
        ctx.fillStyle = s"rgba(0,191,255,${point.trans})"
        ctx.arc(
          point.point.x * scale + offsetPosition.x,
          point.point.y * scale + offsetPosition.y,
          (pearl.radius + 2) * scale - (0.2 - point.trans) * 25,
          0,
          math.Pi * 2)
        ctx.fill()
        ctx.restore()
      }
        pearlPositionList = PearlTail(Point(pearl.position.x + offsetPoint.x, pearl.position.y + offsetPoint.y), 0.2f) :: pearlPositionList
        pearlPositionList.foreach(p => p.trans = p.trans - 0.008f)
        pearlPositionList = pearlPositionList.take(24)
      }

      //主体
      ctx.save()
      ctx.beginPath()
      val grd = ctx.createRadialGradient(
        (pearl.position.x + offsetPoint.x) * scale + offsetPosition.x,
        (pearl.position.y + offsetPoint.y) * scale + offsetPosition.y,
        pearl.radius * scale / 2,
        (pearl.position.x + offsetPoint.x) * scale + offsetPosition.x,
        (pearl.position.y + offsetPoint.y) * scale + offsetPosition.y,
        pearl.radius * scale
      )
      grd.addColorStop(0, "#7adeff")
      grd.addColorStop(1, "#00bfff")
      ctx.fillStyle = grd
      ctx.strokeStyle = "#00bfff"
      ctx.lineWidth = 3
      ctx.arc(
        (pearl.position.x + offsetPoint.x) * scale + offsetPosition.x,
        (pearl.position.y + offsetPoint.y) * scale + offsetPosition.y,
        pearl.radius * scale,
        0,
        math.Pi * 2)
      ctx.stroke()
      ctx.fill()

      ctx.restore()
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
