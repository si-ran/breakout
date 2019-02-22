package com.neo.sk.breaker.models.dao

import com.neo.sk.breaker.models.SlickTables
import com.neo.sk.utils.DBUtil.db
import com.neo.sk.breaker.models.SlickTables._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
/**
  * User: XuSiRan
  * Date: 2019/2/18
  * Time: 0:43
  */
object UserInfoDAO {

  def userLogin(account: String) ={
    db.run(tUserInfo.filter(t => t.account === account).map(t => (t.userName, t.password, t.ban)).result.headOption)
  }

  def getOneInfo(account: String) ={
    db.run(tUserInfo.filter(t => t.account === account).map(t => (t.userName, t.ban)).result.headOption)
  }

  def getOneInfoByName(name: String) ={
    db.run(tUserInfo.filter(t => t.userName === name).map(t => (t.userName, t.ban)).result.headOption)
  }

  def SignUp(name: String, account: String, password: String, ban:Boolean = false) ={
    db.run(tUserInfo.map(t => (t.userName, t.account, t.password, t.ban)) += (name, account, password, ban))
  }

  def banUser(name: String) ={
    db.run(tUserInfo.filter(t => t.userName === name).map(_.ban).update(true))
  }

  def unBanUser(name: String) ={
    db.run(tUserInfo.filter(t => t.userName === name).map(_.ban).update(false))
  }

  def allInfo() ={
    db.run(tUserInfo.result)
  }

}
