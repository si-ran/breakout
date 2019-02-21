package com.neo.sk.breaker.front.common

/**
  * User: Taoz
  * Date: 2/24/2017
  * Time: 10:59 AM
  */
object Routes {


  val baseUrl = "/breaker"

  def wsJoinGameUrl(name:String): String = baseUrl + s"/gameJoin?name=$name"

  object Login{

    val base: String = baseUrl + "/login"

    def userLogin: String = base + "/userLogin"

    def adminLogin: String = base + "/adminLogin"

  }

  object Admin{

    val base: String = baseUrl + "/admin"

    def getStatics: String = base + "/getStatics"

  }










}