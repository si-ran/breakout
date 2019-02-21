package com.neo.sk.breaker.shared.ptcl.breaker

import com.neo.sk.breaker.shared.ptcl.component.CircleOfBreaker
import com.neo.sk.breaker.shared.ptcl.model
import com.neo.sk.breaker.shared.ptcl.model.Point

/**
  * Created by Jingyi on 2018/11/9
  */
case class PearlState(
  position: Point
)

trait Pearl extends CircleOfBreaker{

  override var position: Point

  override val radius: Float

  var speed: Point //速度(用点表示方向和速度大小)

  var isShow: Boolean

}

class PearlClient(initPosition: Point, initSpeed: Point = Point(20, -20)) extends Pearl{

  override var position: Point = initPosition

  override val radius: Float = 10

  override var speed: Point = initSpeed

  override var isShow: Boolean = true


}

