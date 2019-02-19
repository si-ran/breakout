package com.neo.sk.breaker.models.dao

import com.neo.sk.utils.DBUtil.db
import com.neo.sk.breaker.models.SlickTables._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
/**
  * User: XuSiRan
  * Date: 2019/2/18
  * Time: 0:43
  */
object LoginDAO {

  def userLogin(account: String) ={
    db.run(tUserInfo.filter(t => t.account === account).map(t => (t.userName, t.password)).result.headOption)
  }

}
