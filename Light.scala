package com.oring.smartcity.example

/**
  * Created by PeterHuang on 2016/8/5.
  */

abstract class Light {
  var id: String
  var br: String
  var v: String
  var i: String
  var gps: String
}

object Light {
  var listLight: List[Light] = List()
  private class NewLight (val setID: String, val setBR: String, val setV: String, val setI: String, val setG: String) extends Light{
    override var id = setID
    override var br = setBR
    override var v = setV
    override var i = setI
    override var gps = setG
    override def toString = (
      "\"destinationID\":\"10000000\"," +
        "\"sourceID\":\"%s\"," +
        "\"header\":\"5A\",").format(id)
  }
  def makeLight(id: String, br: String, v: String, i: String, gps: String): Light =
    new NewLight(id, br, v, i, gps)
  def addLight(light: Light): Unit ={
    listLight = light :: listLight
  }
}
