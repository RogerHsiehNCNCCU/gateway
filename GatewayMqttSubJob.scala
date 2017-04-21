package com.oring.smartcity.example

import java.io.{File, FileInputStream}

import com.oring.smartcity.makka.{Data, Job}
import org.eclipse.paho.client.mqttv3._
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import play.api.libs.json.Json

/**
  * Created by WeiChen on 2016/6/17.
  */

class GatewayMqttSubJob extends Job{
  override def init(): Unit = {
    println("Job GatewayMqttSub & Connections init !")
    val in = new FileInputStream(new File("src/main/resources/mqttConf.json"))
    val rawJson = Json.parse(in)
    //val topics = (rawJson \ "topic").as[Array[String]]
    val topics = (rawJson \ "topic").as[String]
    val mqttBrokerHost = "ssl://"+(rawJson \ "broker").as[String]
    println(mqttBrokerHost, topics)
    
    //Set up persistence for messages
    val persistence = new MemoryPersistence
    val options = new MqttConnectOptions
    val sslContext = new SSL().getSSLcontext("mytest","src/main/resources/cacerts")

    options.setSocketFactory(sslContext.getSocketFactory)
    options.setUserName("test")
    options.setPassword("test".toCharArray())

    val client = new MqttClient(mqttBrokerHost, MqttClient.generateClientId, persistence)

    //Connect to MqttBroker
    client.connect(options)
    println(client.isConnected())

    client.subscribe(topics)
    val callback = new MqttCallback {
      override def messageArrived(topic: String, message: MqttMessage): Unit = {
        val rawJson = Json.parse(message.toString)
        val dest = (rawJson \ "message" \ "destinationID").as[String]
        val src = (rawJson \ "message" \ "sourceID").as[String]
        val header = (rawJson \ "message" \ "header").as[String]
        val command = (rawJson \ "message" \ "command").as[String]
        val length = (rawJson \ "message" \ "length").as[String]
        val value = (rawJson \ "message" \ "value").as[String]
        val msg = dest + "," + src + "," + header + "," + command + "," + length + "," + value
        val jsonStr = "{\"topic\":\"%s\",\"message\":\"%s\"}".format(topic, msg)
        println(jsonStr)
        val responseData = new Data(jsonStr)
        pipe("GatewayJob", responseData)
      }

      override def connectionLost(cause: Throwable): Unit = {
        println(cause)
      }

      override def deliveryComplete(token: IMqttDeliveryToken): Unit = {
      }
    }
    client.setCallback(callback)
  }

  override def receiveData(d: String): Unit = {
  }
}
