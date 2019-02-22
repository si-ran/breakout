package com.neo.sk.breaker.models.dao

import com.neo.sk.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
/**
  * User: XuSiRan
  * Date: 2019/2/18
  * Time: 0:43
  */
case class UserInfo(id: Int , name: String, account: String, password: String, win: Int, isBan: Boolean)

trait UserInfoTable{
  import com.neo.sk.utils.DBUtil.driver.api._

  class UserInfoTable(tag: Tag) extends Table[UserInfo](tag, "USER_INFO") {
    def * = (id, name, account, password, win, isBan) <> (UserInfo.tupled, UserInfo.unapply)

    val id = column[Int]("ID", O.AutoInc, O.PrimaryKey)
    val name = column[String]("NAME")
    val account = column[String]("ACCOUNT")
    val password = column[String]("PASSWORD")
    val win = column[Int]("WIN")
    val isBan = column[Boolean]("IS_BAN")

    /** Uniqueness Index over (userId) (database name user_info_user_id_index) */
  }

  protected val userInfoTableQuery = TableQuery[UserInfoTable]
}

object UserInfoDAO extends UserInfoTable {

  def userLogin(account: String) ={
    db.run(userInfoTableQuery.filter(t => t.account === account).map(t => (t.name, t.password, t.isBan)).result.headOption)
  }

  def getOneInfo(account: String) ={
    db.run(userInfoTableQuery.filter(t => t.account === account).map(t => (t.name, t.isBan)).result.headOption)
  }

  def getOneInfoByName(name: String) ={
    db.run(userInfoTableQuery.filter(t => t.name === name).map(t => (t.name, t.isBan)).result.headOption)
  }

  def SignUp(name: String, account: String, password: String, ban:Boolean = false) ={
    db.run(userInfoTableQuery.map(t => (t.name, t.account, t.password, t.win, t.isBan)) += ( name, account, password, 0, ban))
  }

  def banUser(name: String) ={
    db.run(userInfoTableQuery.filter(t => t.name === name).map(_.isBan).update(true))
  }

  def unBanUser(name: String) ={
    db.run(userInfoTableQuery.filter(t => t.name === name).map(_.isBan).update(false))
  }

  def allInfo() ={
    db.run(userInfoTableQuery.result)
  }

}
