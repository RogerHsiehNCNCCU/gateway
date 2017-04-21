package com.oring.smartcity.example

import com.oring.smartcity.makka.MSController
/**
  * Created by PeterHuang on 2016/7/21.
  */
object GatewayDemoMain {
  def main(args: Array[String]) {
    //val path = "src/main/resources/exampleConfig.json"
    val msc = new MSController()
    msc.run()
  }
}