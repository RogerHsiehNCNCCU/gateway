package com.oring.smartcity.example

import java.util.Calendar
import java.io._

import com.oring.smartcity.makka.Job
import org.eclipse.paho.client.mqttv3._
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import play.api.libs.json.{JsValue, Json}

class GatewayMqttPubJob extends Job{
  override def init(): Unit = {
    println("Job GatewayMqttPub & Connections init !")
  }

  override def receiveData(d: String): Unit = {
    var client : MqttClient = null
    val dataJson = Json.parse(d)
    val topic = (dataJson \ "topic").as[String]
    //val topic = "GATEWAY/RES/TEST"
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
      val message = new MqttMessage(d.getBytes("utf-8"))
      msgTopic.publish(message)
      val rightnow = Calendar.getInstance()
      val mill = rightnow.getTimeInMillis()
      //val whattime = rightnow.getTime().toString()
      val year = rightnow.get(Calendar.YEAR)
      val month = rightnow.get(Calendar.MONTH)+1 // index from 0
      val day = rightnow.get(Calendar.DAY_OF_MONTH)
      val filePath = "logs/"
      val file = new File(filePath)
      if(!file.exists())
      {
        file.mkdirs() //create directory
        /*
        * try{
        *   create
        * }
        * catch{
        *   case e:
        * }
        * */
      }
      val writer = new PrintWriter(new File("./logs/"+year+month+day+mill+"test.json" )) //write to new file in current directory
      //but the directory 顯示在IoT-gateway-master那
      writer.write(message.toString)
      writer.close()
      println("Publishing Data, Topic : %s, Message : %s\n".format(msgTopic.getName, message))
    }

    catch {
      case e: MqttException => println("Exception Caught: " + e)
    }

    finally {
      client.disconnect()
    }
  }
}
