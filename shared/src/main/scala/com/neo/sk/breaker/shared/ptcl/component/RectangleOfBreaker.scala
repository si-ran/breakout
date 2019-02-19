package com.neo.sk.breaker.shared.ptcl.component

import com.neo.sk.breaker.shared.ptcl.model

/**
  * User: XuSiRan
  * Date: 2019/1/30
  * Time: 18:52
  */
trait RectangleOfBreaker extends ObjectOfBreaker {

  override var position: model.Point

  val width: Float
  val height: Float

}
