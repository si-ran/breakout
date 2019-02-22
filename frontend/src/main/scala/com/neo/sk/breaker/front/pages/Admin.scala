package com.neo.sk.breaker.front.pages

import com.neo.sk.breaker.front.common.Page
import com.neo.sk.breaker.front.utils.{Http, JsFunc}
import com.neo.sk.breaker.front.common.Routes
import com.neo.sk.breaker.shared.ptcl.protocol.AdminProtocol._
import com.neo.sk.breaker.shared.ptcl.protocol.SuccessRsp
import org.scalajs.dom
import io.circe.generic.auto._
import io.circe.syntax._
import mhtml.Var

import scala.xml.Elem
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * User: XuSiRan
  * Date: 2019/2/18
  * Time: 12:05
  */
object Admin extends Page{

  val indexData = Var(List.empty[RoomStatics])
  val indexUserData = Var(List.empty[OneUserStatic])

  def obtainGameStatics(): Unit ={
    Http.getAndParse[GameStaticsRsp](Routes.Admin.getStatics).map {
      static: GameStaticsRsp =>
        if (static.errCode == 0) {
          indexData := static.roomStatics
        }
        else {
          JsFunc.alert("error")
        }
    }
  }

  def obtainUserStatics(): Unit ={
    Http.getAndParse[UserStaticsRsp](Routes.Admin.getUserInfo).map {
      infos: UserStaticsRsp =>
        if(infos.errCode == 0) {
          indexUserData := infos.userStatics
        }
        else{
          JsFunc.alert("error, get userInfo")
        }
    }
  }

  def banUser(userName: String): Unit ={
    Http.postJsonAndParse[SuccessRsp](Routes.Admin.banUser, BanUserReq(userName).asJson.noSpaces).map {
      rsp: SuccessRsp =>
        if(rsp.errCode == 0)
          JsFunc.alert("成功禁用/解禁")
        else
          JsFunc.alert(s"${rsp.msg}")
    }
  }

  val recordSheet : Elem =
    <div>
      <div class="RLTableTitleRow">
        <h2 class="RLTableTitle">房间管理</h2>
      </div>
      <div class="RLTableTitleText" style={s"width: ${dom.window.innerWidth}px"}></div>
      <div class="RLTableIndexRow">
        <form></form>
      </div>
      {indexData.map {
      case Nil =>
        <div id="warnDiv" style="margin-left: 30px">
          没有数据
        </div>
      case list =>
        <div>
          <table id="bot_table" class="table table-striped RLTable">
            <thead>
              <tr>
                <th>房间号</th>
                <th>用户</th>
                <th>用户</th>
              </tr>
            </thead>
            <tbody>
              {list.map {
              info =>
                <tr>
                  <td style = "width: 40%;">{info.id}</td>
                  <td>{info.user1}<button type="button" id="RLBotListGetButton" class="btn btn-primary" onclick={()=> banUser(info.user1)}>禁用/解禁</button></td>
                  <td>{info.user2}<button type="button" id="RLBotListGetButton" class="btn btn-primary" onclick={()=> banUser(info.user2)}>禁用/解禁</button></td>
                </tr>
            }}
            </tbody>
          </table>
        </div>
    }}
    </div>

  val userSheet : Elem =
    <div>
      <div class="RLTableTitleRow">
        <h2 class="RLTableTitle">用户管理</h2>
      </div>
      <div class="RLTableTitleText" style={s"width: ${dom.window.innerWidth}px"}></div>
      <div class="RLTableIndexRow">
        <form></form>
      </div>
      {indexUserData.map {
      case Nil =>
        <div id="warnDiv" style="margin-left: 30px">
          没有数据
        </div>
      case list =>
        <div>
          <table id="bot_table" class="table table-striped RLTable">
            <thead>
              <tr>
                <th>用户</th>
                <th>禁用状态</th>
                <th>获胜次数</th>
              </tr>
            </thead>
            <tbody>
              {list.map {
              info =>
                <tr>
                  <td style = "width: 20%;">{info.name}</td>
                  <td>{if(info.isBan) "yes" else "no"}<button type="button" id="RLBotListGetButton" class="btn btn-primary" onclick={()=> banUser(info.name)}>禁用/解禁</button></td>
                  <td>{info.win}</td>
                </tr>
            }}
            </tbody>
          </table>
        </div>
    }}
    </div>


  override def render: Elem = {
    obtainGameStatics()
    obtainUserStatics()
    <div>
      {recordSheet}
      {userSheet}
    </div>
  }

}
