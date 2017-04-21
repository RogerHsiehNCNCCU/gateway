package com.oring.smartcity.example

import java.io.{File, FileInputStream}

import com.oring.smartcity.makka.Job
import org.eclipse.paho.client.mqttv3._
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import play.api.libs.json.Json
import play.api.libs.json.Json.JsValueWrapper

import scala.io.Source

class CMSMqttPubJob extends Job{
  override def init(): Unit = {
    var client : MqttClient = null
    val topic = "GATEWAY/REQ"
    //val msg = "gateway2-message00.json"
    //var i =  0
    // Creating new persistence for mqtt client
    //val persistence = new MqttDefaultFilePersistence("/tmp")
    val persistence = new MemoryPersistence

    try {
      // mqtt client with specific url and client id
      client = new MqttClient("ssl://140.119.19.99:8883", MqttClient.generateClientId, persistence)

      val options = new MqttConnectOptions
      val sslContext = new SSL().getSSLcontext("mytest","src/main/resources/cacerts")
      options.setSocketFactory(sslContext.getSocketFactory)
      options.setUserName("test")
      options.setPassword("test".toCharArray())
      client.connect(options)
      println( client.isConnected())

      val msgTopic = client.getTopic(topic)
      for(i <- 0 to 9){
        val in = new FileInputStream(new File("src/main/resources/message" + i + ".json"))
        val rawJson = Json.parse(in)
        val msg = Json.stringify(rawJson)
        val message = new MqttMessage(msg.getBytes("utf-8"))
        msgTopic.publish(message)
        println("Publishing Data, Topic : %s, Message : %s".format(msgTopic.getName, message))
        Thread.sleep(10000)
      }
    }

    catch {
      case e: MqttException => println("Exception Caught: " + e)
    }

    finally {
      client.disconnect()
    }
  }

  override def receiveData(d: String): Unit = {
  }
}
