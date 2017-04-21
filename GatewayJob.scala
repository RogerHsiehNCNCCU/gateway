package com.oring.smartcity.example

import com.oring.smartcity.makka.{Data, Job}
import play.api.libs.json.Json
import java.util.Calendar
import com.oring.smartcity.example.Light.listLight


class GatewayJob extends Job {
  override def init(): Unit = {
    println("Job Gateway & Connections init !")
  }

  override def receiveData(data: String): Unit = {
    val dataJson = Json.parse(data)
    val gatewayData = (dataJson \ "message").as[String]
    val dataList: List[String] = gatewayData.split(",").toList
    findLight(dataList)
  }

  def findLight(receiveLightData: List[String]): Unit = {
    println("Total light = " + listLight.length)
    val matchLight = listLight.filter(_.id == receiveLightData.head)
    receiveLightData(3) match {
      case "00" | "05" | "07" => setLight(receiveLightData,matchLight)
      case "01" | "02" | "03" | "04" | "06" => sendData(receiveLightData, matchLight)
      case _ => println("Command is Wrong!")
    }
  }

  def setLight(lightData: List[String], matchLight: List[Light]): Unit = {
    lightData(3) match {
      case "00" =>
        matchLight.head.br = lightData(5)
        println("Set " + matchLight.head.id + " Brightness to " + matchLight.head.br)
      case "05" =>
        matchLight.head.id = lightData(0)
        println("Set light " + matchLight.head.id + " ID to " + matchLight.head.id)
      case "07" =>
        matchLight.head.br = lightData(5)
        println("Set " + matchLight.head.id + " Default Brightness to " + matchLight.head.br)
      case _ => println("Command is Wrong!")
    }
  }

  def sendData(lightData: List[String], matchLight: List[Light]): Unit = {
    val topic = "GATEWAY/RES"
    def setOutput(command: String, commandValue: String): String = {
      val t = matchLight.head.toString + "\"command\":\"" + lightData(3) + "\",\"length\":\"01\",\"value\":\"" + commandValue + "\""
      println("Get light " + matchLight.head.id + " " + command + " " + commandValue)
      t
    }
    def sendJson(str: String): Unit = {
      val jsonStr = "{\"topic\":\"%s\",\"message\":{%s},\"timestamp\":\"%s\"}".format(topic, str, Calendar.getInstance().getTimeInMillis.toString)
      println(jsonStr)
      val responseData = new Data(jsonStr)
      pipe("GatewayMqttPubJob", responseData)
    }
    lightData(3) match {
      case "01" =>
        sendJson(setOutput("Voltage",matchLight.head.v))
      case "02" =>
        sendJson(setOutput("Current",matchLight.head.i))
      case "03" =>
        sendJson(setOutput("GPS",matchLight.head.gps))
      case "04" =>
        sendJson(setOutput("ID",matchLight.head.id))
      case "06" =>
        sendJson(setOutput("Brightness",matchLight.head.br))
      case _ => println("Command is Wrong!")
    }
  }
}
