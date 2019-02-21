package com.neo.sk.breaker.front.breaker

import com.neo.sk.breaker.shared.ptcl.protocol.BreakerEvent._

/**
  * User: XuSiRan
  * Date: 2019/2/14
  * Time: 19:20
  */
trait BreakerSchemaImplClient {

  var renderControl: Int = 1 //用以切换画面状态（等待，读取等在GameHolder中可以直接渲染的内容）

  def drawGameByTime(offsetTime:Long): Unit ={}

  def logicUpdate(): Unit

  def preExecuteUserEvent(event: GamePlayEvent): Unit

  //立即执行的事件，返回Int状态，用于改变Canvas的显示
  def instantExecuteUserEvent(event: GamePlayEvent): Int

}
