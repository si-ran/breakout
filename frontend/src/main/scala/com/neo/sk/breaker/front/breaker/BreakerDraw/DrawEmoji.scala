package com.neo.sk.breaker.front.breaker.BreakerDraw

import com.neo.sk.breaker.front.breaker.BreakerDoublePlay
import com.neo.sk.breaker.shared.ptcl.model.Point
import org.scalajs.dom
import org.scalajs.dom.html

/**
  * User: XuSiRan
  * Date: 2019/2/18
  * Time: 22:13
  */
trait DrawEmoji { this: BreakerDoublePlay =>

  def drawEmojiList(): Unit ={
    val emojiView: Point = Point(70, 135)

//    ctx.save()
//    ctx.beginPath()
//    ctx.rect(emojiView.x, emojiView.y, 160, 420)
//    ctx.stroke()
//    ctx.restore()

    var cnt = 0
    emojiList.take(3).foreach{ emoji =>
      cnt += 1
      val image = dom.document.createElement("img").asInstanceOf[html.Image]
      image.setAttribute("src", s"/breaker/static/img/emoji-${emoji.t}.png")
//      image.setAttribute("src", s"/breaker/static/img/Menhera-${emoji.t}.png")

      ctx.save()
      ctx.drawImage(image, emojiView.x + 15, 575 - 135 * cnt, 129.5, 112)
      ctx.font = "15px arial"
      ctx.fillText(s"${emoji.user}", emojiView.x + 5, 560 - 135 * cnt)
      ctx.restore()
    }
  }

  def drawScore(): Unit ={
    ctx.save()
    ctx.font = "30px Comic Sans Ms"
    ctx.textAlign = "left"
    ctx.fillText(s"$gameScore", 260, 530)
    ctx.restore()
  }

}
