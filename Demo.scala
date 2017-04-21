/**
  * Created by a0375 on 2016/8/12.
  */

import java.io._

object Demo {
  def main(args: Array[String]) {
    val writer = new PrintWriter(new File("./123/test.txt" ))//可增加路徑

    writer.write("Hello Scalaaaaaa")
    writer.close()
  }
}