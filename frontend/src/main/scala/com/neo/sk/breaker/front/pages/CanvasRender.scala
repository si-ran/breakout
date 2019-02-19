package com.neo.sk.breaker.front.pages

import com.neo.sk.breaker.front.common.Page
import com.neo.sk.breaker.front.breaker.GameHolder
import com.neo.sk.breaker.front.utils.Shortcut
import com.neo.sk.breaker.shared.ptcl.model.Point
import mhtml.Var
import org.scalajs.dom
import org.scalajs.dom.ext.Color
import org.scalajs.dom.html.Canvas
import mhtml._
import scala.xml.Elem

/**
  * Created by Jingyi on 2018/11/9
  */
class CanvasRender(name: String) extends Page{

  private val canvas = <canvas id ="GameView" tabindex="1"></canvas>

  def init(): Unit = {
    val gameHolder = new GameHolder("GameView")
    gameHolder.start(name)

  }



  override def render: Elem ={
    Shortcut.scheduleOnce(() =>init(),0)
    <div>
      {canvas}
    </div>
  }



}
