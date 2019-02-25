package com.neo.sk.breaker.front.pages

import com.neo.sk.breaker.front.common.Page
import com.neo.sk.breaker.front.utils.{Http, JsFunc}
import com.neo.sk.breaker.front.common.Routes
import com.neo.sk.breaker.shared.ptcl.protocol.LoginProtocol.{LoginReq, LoginUserRsp, SignUpReq}
import com.neo.sk.breaker.shared.ptcl.protocol.{ComRsp, CommonRsp, ErrorRsp, SuccessRsp}
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

  val fromContentFlag = Var(3)

  def signUp(): Unit = {
    val name = dom.document.getElementById("userName").asInstanceOf[Input].value
    val account = dom.document.getElementById("userAccount").asInstanceOf[Input].value
    val password = dom.document.getElementById("userPassword").asInstanceOf[Input].value
    val rePassword = dom.document.getElementById("userPasswordReEnter").asInstanceOf[Input].value

    Http.postJsonAndParse[ComRsp](Routes.Login.signUp, SignUpReq(name, account, password).asJson.noSpaces).map {
      case ComRsp(0, "ok") =>
        JsFunc.alert("注册成功")
      case ComRsp(errCode, msg) =>
        JsFunc.alert(s"$msg")

    }
  }

  def userLogin():Unit ={
    val account = dom.document.getElementById("loginAccount").asInstanceOf[Input].value
    val password = dom.document.getElementById("loginPassword").asInstanceOf[Input].value

    if(account.isEmpty){
      JsFunc.alert(s"账号不能为空")
    }
    else {
      Http.postJsonAndParse[LoginUserRsp](Routes.Login.userLogin, LoginReq(account, password).asJson.noSpaces).map{
        case LoginUserRsp(name, 0, "ok") =>
          dom.window.localStorage.setItem("user", s"$name")
          dom.window.location.hash = s"/play/$name"
        case LoginUserRsp(_, errCode, msg) =>
          JsFunc.alert(s"$msg")
      }
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

  def guestLogin(): Unit ={
    val nickname = dom.document.getElementById("nickname").asInstanceOf[Input].value
    if(nickname.nonEmpty){
      dom.window.localStorage.setItem("user", s"guest-${System.currentTimeMillis() % 10000}-$nickname")
      dom.window.location.hash = s"/play/guest-${System.currentTimeMillis() % 10000}-$nickname"
    }
    else JsFunc.alert("error: nickname is none")
  }

  val fromContent: Rx[Elem] = fromContentFlag.map{
    case 0 =>
      <section class="signUpFrom">
        <div class="form-title">
          <h2>注册</h2>
        </div>
        <div class="form-content">
          <input class="form-control" id="userName" placeholder="userName"></input>
          <input class="form-control" id="userAccount" placeholder="userAccount"></input>
          <input type="password" class="form-control" id="userPassword" placeholder="password"></input>
          <input type="password" class="form-control" id="userPasswordReEnter" placeholder="re_enter password"></input>
        </div>
        <div class="form-submit">
          <button class="btn" onclick={()=> signUp()}>注册</button>
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
        </div>
        <div class="form-tip">
          <span>如果你还没有账号 <a onclick={()=> fromContentFlag := 0} style="cursor:pointer;">点击这里</a></span>
          <span style="display: block;">或者以游客身份登录 <a onclick={()=> fromContentFlag := 3} style="cursor:pointer;">点击这里</a></span>
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
          <button class="btn" onclick={()=> adminLogin()}>登录</button>
        </div>
        <div class="form-tip">
        </div>
        <div class="form-information">
        </div>
      </section>
    case 3 =>
      <section class="signUpFrom">
        <div class="form-title">
          <h2>游客登录</h2>
        </div>
        <div class="form-content">
          <input class="form-control" id="nickname" placeholder="userNickname"></input>
        </div>
        <div class="form-submit">
          <button class="btn" onclick={()=> guestLogin()}>游客登录</button>
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
          <li class="nav-item">
            <a class={if(flag == 3) "nav-link active" else "nav-link"} onclick={()=> fromContentFlag := 3}>游客</a>
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
