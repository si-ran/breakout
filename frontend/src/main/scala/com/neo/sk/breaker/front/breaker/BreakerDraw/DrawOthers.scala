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
trait DrawOthers { this: BreakerDoublePlay =>

  def drawBack(): Unit ={
    ctx.save()
    val grd = ctx.createLinearGradient(
      0,
      0,
      0,
      500,
    )
    grd.addColorStop(0, "#f7ebff")
    grd.addColorStop(1, "#ffffff")
    ctx.fillStyle = grd
    ctx.fillRect(0,0,canvasBoundary.x,500)
    ctx.restore()
  }

  def drawBorder(name: String, scale: Double, offsetPosition: Point): Unit ={
    val fontSize = if(name == myName) "21px" else "15px"

    ctx.save()
    ctx.beginPath()
    ctx.font = s"$fontSize arial"
    ctx.textAlign = "left"
    ctx.fillText(s"$name", offsetPosition.x, offsetPosition.y - 3)
    ctx.rect(offsetPosition.x, offsetPosition.y, boundary.x * scale, boundary.y * scale)
    ctx.stroke()
    ctx.restore()
    //提示
    ctx.save()
    ctx.font = s"17px arial"
    ctx.fillStyle = "#666666"
    ctx.textAlign = "left"
    ctx.fillText("A: 左移; D: 右移", 1100, 50)
    ctx.fillText("Q: 消耗5P释放散弹", 1100, 80)
    ctx.fillText("鼠标左右键: 平移小球", 1100, 110)
    ctx.fillText("1~8: 表情", 1100, 140)
    ctx.fillText("表情1: Good", 1100, 200)
    ctx.fillText("表情2: Hello", 1100, 220)
    ctx.fillText("表情3: #%$", 1100, 240)
    ctx.fillText("表情4: $#%", 1100, 260)
    ctx.fillText("表情5: #$*", 1100, 280)
    ctx.fillText("表情6: |||", 1100, 300)
    ctx.fillText("表情7: ???", 1100, 320)
    ctx.fillText("表情8: Bye bye", 1100, 340)
    ctx.restore()
    //红线
    ctx.save()
    ctx.beginPath()
    ctx.moveTo(offsetPosition.x, offsetPosition.y + boundary.y * scale * 19 / 25)
    ctx.lineTo(offsetPosition.x + boundary.x  * scale, offsetPosition.y + boundary.y  * scale * 19 / 25)
    ctx.strokeStyle = "red"
    ctx.stroke()
    ctx.restore()
  }

  def drawEmojiList(): Unit ={
    val emojiView: Point = Point(50, 135)

//    ctx.save()
//    ctx.beginPath()
//    ctx.rect(emojiView.x, emojiView.y, 160, 420)
//    ctx.stroke()
//    ctx.restore()

    var cnt = 0
    emojiList.take(3).foreach{ emoji =>
      cnt += 1
      val image = dom.document.createElement("img").asInstanceOf[html.Image]
//      image.setAttribute("src", s"/breaker/static/img/emoji-${emoji.t}.png")
      image.setAttribute("src", s"/breakoutSIRAN/static/img/Menhera-${emoji.t}.png")

      ctx.save()
      ctx.drawImage(image, emojiView.x + 15, 575 - 135 * cnt, 129.5, 112)
      ctx.font = "15px arial"
      ctx.fillText(s"${emoji.user} 发送表情：", emojiView.x + 5, 570 - 135 * cnt)
      ctx.restore()
    }
  }

  def drawSkillValue(): Unit ={
    ctx.save()
    ctx.font = "30px Comic Sans Ms"
    ctx.textAlign = "left"
    ctx.fillText(s"$gameSkillValue P", 260, 520)
    ctx.restore()
  }

  //依附于显示框
  def drawEnergy(energy: Int, viewPosition: Point, scale: Double): Unit ={
    ctx.save()
    ctx.beginPath()
    ctx.rect(viewPosition.x + boundary.x * scale, viewPosition.y + (boundary.y * scale - boundary.y * scale * energy / 9), 25 * scale, boundary.y * scale * energy / 9)
    ctx.fillStyle = "#ecb883"
    ctx.fill()
    ctx.beginPath()
    ctx.rect(viewPosition.x + boundary.x * scale, viewPosition.y, 25 * scale, boundary.y * scale)
    ctx.stroke()
    ctx.restore()
  }

  def drawShot(): Unit ={
    ctx.save()
    ctx.beginPath()
    ctx.font = "bold 30px Comic Sans Ms"
    ctx.textAlign = "center"
    ctx.fillStyle = "rgba(242, 141, 105, 0.6)"
    ctx.fillText("SHOT!!!", otherWindowView.x + boundary.x * otherWindowScale / 2, otherWindowView.y + boundary.y * otherWindowScale / 3 + 1 * drawShotTime)
    ctx.restore()
    drawShotTime -= 1
  }

  //画提示文字
  def drawText(point: Point): Unit ={
    ctx.save()
    ctx.beginPath()
    ctx.font = "bold 30px Comic Sans Ms"
    ctx.textAlign = "center"
    ctx.fillStyle = "rgba(242, 141, 105, 0.8)"
    ctx.fillText("对方给你加了一层方块", point.x, point.y)
    ctx.restore()
  }

}
