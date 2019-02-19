package com.neo.sk.breaker.front.pages

import com.neo.sk.breaker.front.common.Page
import com.neo.sk.breaker.front.utils.{Http, JsFunc}
import com.neo.sk.breaker.front.common.Routes
import com.neo.sk.breaker.shared.ptcl.protocol.AdminProtocol._
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

  val recordSheet : Elem =
    <div>
      <div class="RLTableTitleRow">
        <h2 class="RLTableTitle">用户管理</h2>
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
                  <td>{info.id}</td>
                  <td>{info.user1}</td>
                  <td>{info.user2}</td>
                </tr>
            }}
            </tbody>
          </table>
        </div>
    }}
    </div>


  override def render: Elem = {
    obtainGameStatics()
    <div>
      {recordSheet}
    </div>
  }

}
