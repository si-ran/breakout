package com.neo.sk.breaker.shared.ptcl.component

import com.neo.sk.breaker.shared.ptcl.model.Point

/**
  * User: XuSiRan
  * Date: 2019/1/30
  * Time: 18:48
  */
trait CircleOfBreaker extends ObjectOfBreaker {

  override var position: Point

  val radius : Float

  //上下左右1234
  final def collided(rect: RectangleOfBreaker): Int ={

    val closeX = {
      if(position.x < rect.position.x) rect.position.x
      else if(position.x > rect.position.x + rect.width) rect.position.x + rect.width
      else position.x
    }
    val closeY = {
      if(position.y < rect.position.y) rect.position.y
      else if(position.y > rect.position.y + rect.height) rect.position.y + rect.height
      else position.y
    }

    val isCollided =
      if(position < (rect.position + Point(rect.width, rect.height)) && position > rect.position) true
      else math.sqrt((closeX - position.x) * (closeX - position.x) + (closeY - position.y) * (closeY - position.y)) <= radius + 1

    if(isCollided){
      if(math.abs(closeY - rect.position.y) < 1) 1
      else if(math.abs(closeY - rect.position.y - rect.height) < 1) 2
      else if(math.abs(closeX - rect.position.x) < 1) 3
      else if(math.abs(closeX - rect.position.x - rect.width) < 1) 4
      else 1
    }
    else 0



  }

}
