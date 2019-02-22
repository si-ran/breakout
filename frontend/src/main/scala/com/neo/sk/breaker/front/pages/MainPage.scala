package com.neo.sk.breaker.front.pages

import com.neo.sk.breaker.front.common.{Page, PageSwitcher}
import mhtml.{Cancelable, Rx, Var, mount}
import org.scalajs.dom

import scala.xml.Elem


object MainPage extends PageSwitcher {



  private val currentPage: Rx[Elem] = currentHashVar.map {
    case "login" :: Nil => Login.render
    case "admin" :: Nil => Admin.render
    case "play" :: name :: Nil => new CanvasRender(name).render
    case _ => Login.render
  }


  def show(): Cancelable = {
    switchPageByHash()
    val page =
      <div>
        {currentPage}
      </div>
    mount(dom.document.body, page)
  }

}
