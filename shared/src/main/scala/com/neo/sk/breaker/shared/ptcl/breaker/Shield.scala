package com.neo.sk.breaker.shared.ptcl.breaker
import com.neo.sk.breaker.shared.ptcl.component.RectangleOfBreaker
import com.neo.sk.breaker.shared.ptcl.model
import com.neo.sk.breaker.shared.ptcl.model.Point

/**
  * Created by Jingyi on 2018/11/9
  * From Thor to Breaker on 2019/1/29
  */

//传输Shield的基本状态
case class ShieldState(
  position: Short,
  speed: Byte
)

trait Shield extends RectangleOfBreaker{

  override var position : Point

  override val width: Float

  override val height: Float

  val speed: Int

}

class ShieldClient(initPosition: Point) extends Shield {

  override var position: Point = initPosition

  override val width: Float = 160

  override val height: Float = 40

  override val speed: Int = 40

  def changePosition(clientX: Float): Unit ={
    if(clientX < 10000){
      position = Point(clientX, position.y)
    }
    else if(clientX < 20000){
      position = Point(position.x - speed, position.y)
    }
    else{
      position = Point(position.x + speed, position.y)
    }
  }
}