package com.neo.sk.breaker.front.breaker

import com.neo.sk.breaker.shared.ptcl.protocol.BreakerEvent._

/**
  * User: XuSiRan
  * Date: 2019/2/14
  * Time: 19:20
  */
trait BreakerSchemaImplClient {

  def drawGameByTime(offsetTime:Long): Unit ={}

  def logicUpdate(): Unit

  def preExecuteUserEvent(event: GamePlayEvent): Unit

  //立即执行的事件，返回Int状态，用于改变Canvas的显示
  def instantExecuteUserEvent(event: GamePlayEvent): Int

}
