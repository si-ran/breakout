package com.neo.sk.breaker.models

// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object SlickTables extends {
  val profile = slick.jdbc.PostgresProfile
} with SlickTables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait SlickTables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = tUserInfo.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table tUserInfo
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param userName Database column user_name SqlType(varchar), Length(63,true)
   *  @param account Database column account SqlType(varchar), Length(127,true)
   *  @param password Database column password SqlType(varchar), Length(127,true) */
  case class rUserInfo(id: Long, userName: String, account: String, password: String)
  /** GetResult implicit for fetching rUserInfo objects using plain SQL queries */
  implicit def GetResultrUserInfo(implicit e0: GR[Long], e1: GR[String]): GR[rUserInfo] = GR{
    prs => import prs._
    rUserInfo.tupled((<<[Long], <<[String], <<[String], <<[String]))
  }
  /** Table description of table user_info. Objects of this class serve as prototypes for rows in queries. */
  class tUserInfo(_tableTag: Tag) extends profile.api.Table[rUserInfo](_tableTag, "user_info") {
    def * = (id, userName, account, password) <> (rUserInfo.tupled, rUserInfo.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(userName), Rep.Some(account), Rep.Some(password)).shaped.<>({r=>import r._; _1.map(_=> rUserInfo.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column user_name SqlType(varchar), Length(63,true) */
    val userName: Rep[String] = column[String]("user_name", O.Length(63,varying=true))
    /** Database column account SqlType(varchar), Length(127,true) */
    val account: Rep[String] = column[String]("account", O.Length(127,varying=true))
    /** Database column password SqlType(varchar), Length(127,true) */
    val password: Rep[String] = column[String]("password", O.Length(127,varying=true))
  }
  /** Collection-like TableQuery object for table tUserInfo */
  lazy val tUserInfo = new TableQuery(tag => new tUserInfo(tag))
}
