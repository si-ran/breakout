package com.neo.sk.breaker.shared.ptcl.breaker

import com.neo.sk.breaker.shared.ptcl.component.RectangleOfBreaker
import com.neo.sk.breaker.shared.ptcl.model
import com.neo.sk.breaker.shared.ptcl.model.Point

/**
  * User: XuSiRan
  * Date: 2019/2/10
  * Time: 14:29
  */
case class BrickState(
  position: Point
)

trait Brick extends RectangleOfBreaker {

  override var position: Point

  override val width: Float

  override val height: Float

  val color: Byte

}

class BrickClient(point: Point, c: Byte) extends Brick{

  override var position: Point = point

  override val width: Float = 80

  override val height: Float = 40

  override val color: Byte = c

}
