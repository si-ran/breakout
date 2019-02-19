package com.neo.sk.breaker.front.pages

import com.neo.sk.breaker.front.common.Page
import com.neo.sk.breaker.front.utils.{Http, JsFunc}
import com.neo.sk.breaker.front.common.Routes
import com.neo.sk.breaker.shared.ptcl.protocol.LoginProtocol.{LoginReq, LoginUserRsp}
import mhtml.{Rx, Var}
import org.scalajs.dom
import io.circe.generic.auto._
import io.circe.syntax._
import org.scalajs.dom.html.Input

import scala.xml.Elem
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * User: XuSiRan
  * Date: 2019/2/17
  * Time: 23:34
  */
object Login extends Page{

  val fromContentFlag = Var(2)

  def userLogin():Unit ={
    val account = dom.document.getElementById("loginAccount").asInstanceOf[Input].value
    val password = dom.document.getElementById("loginPassword").asInstanceOf[Input].value

    Http.postJsonAndParse[LoginUserRsp](Routes.Login.userLogin, LoginReq(account, password).asJson.noSpaces).map{
      case LoginUserRsp(name, 0, "ok") =>
        dom.window.location.hash = s"/play/$name"
      case LoginUserRsp(_, errCode, msg) =>
        JsFunc.alert(s"$msg")
    }
  }

  def adminLogin():Unit ={
    val account = dom.document.getElementById("loginAccount").asInstanceOf[Input].value
    val password = dom.document.getElementById("loginPassword").asInstanceOf[Input].value

    Http.postJsonAndParse[LoginUserRsp](Routes.Login.adminLogin, LoginReq(account, password).asJson.noSpaces).map{
      case LoginUserRsp(_, 0, "ok") =>
        dom.window.location.hash = s"/admin"
      case LoginUserRsp(_, errCode, msg) =>
        JsFunc.alert(s"$msg")
    }
  }

  val fromContent: Rx[Elem] = fromContentFlag.map{
    case 0 =>
      <section class="signUpFrom">
        <div class="form-title">
          <h2>注册</h2>
        </div>
        <div class="form-content">
          <input class="form-control" id="userEmail" placeholder="userAccount"></input>
          <input type="password" class="form-control" id="userPassword" placeholder="password"></input>
          <input type="password" class="form-control" id="userPasswordReEnter" placeholder="re_enter password"></input>
        </div>
        <div class="form-submit">
          <button class="btn" onclick={()=> }>注册</button>
        </div>
        <div class="form-tip">
          <span>如果你已经注册过了 <a onclick={()=> fromContentFlag := 1} style="cursor:pointer;">点击这里</a></span>
        </div>
        <div class="form-information">
        </div>
      </section>
    case 1 =>
      <section class="signUpFrom">
        <div class="form-title">
          <h2>登录</h2>
        </div>
        <div class="form-content">
          <input class="form-control" id="loginAccount" placeholder="userAccount"></input>
          <input type="password" class="form-control" id="loginPassword" placeholder="password"></input>
        </div>
        <div class="form-submit">
          <button class="btn" onclick={()=> userLogin()}>登录</button>
          <button class="btn" onclick={()=> dom.window.location.hash = s"/play/${System.currentTimeMillis()}"}>游客登录</button>
        </div>
        <div class="form-tip">
          <span>如果你还没有邮箱账号 <a onclick={()=> fromContentFlag := 0} style="cursor:pointer;">点击这里</a></span>
        </div>
        <div class="form-information">
        </div>
      </section>
    case 2 =>
      <section class="signUpFrom">
        <div class="form-title">
          <h2>管理</h2>
        </div>
        <div class="form-content">
          <input class="form-control" id="loginAccount" placeholder="AdminAccount"></input>
          <input type="password" class="form-control" id="loginPassword" placeholder="password"></input>
        </div>
        <div class="form-submit">
          <button class="btn" onclick={()=> }>登录</button>
        </div>
        <div class="form-tip">
        </div>
        <div class="form-information">
        </div>
      </section>
  }

  val cardContent: Rx[Elem] = fromContentFlag.map{ flag =>
    <div class="card text-center" style="pointer-events: all;">
      <div class="card-header">
        <ul class="nav nav-tabs card-header-tabs">
          <li class="nav-item">
            <a class={if(flag == 0) "nav-link active" else "nav-link"} onclick={()=> fromContentFlag := 0}>注册</a>
          </li>
          <li class="nav-item">
            <a class={if(flag == 1) "nav-link active" else "nav-link"} onclick={()=> fromContentFlag := 1}>登录</a>
          </li>
          <li class="nav-item">
            <a class={if(flag == 2) "nav-link active" else "nav-link"} onclick={()=> fromContentFlag := 2}>管理</a>
          </li>
        </ul>
      </div>
      <div class="card-body">
        {fromContent}
      </div>
    </div>
  }

  override def render: Elem =
    <div id="registerPage">
      {cardContent}
    </div>
}
