package com.test.udp

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer


object Main {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("MySystem")
    implicit val materializer = ActorMaterializer()

    val source = ListenUdp(new InetSocketAddress("127.0.0.1", 9876))
    source.runForeach(r => println("Received " + r.data.decodeString("UTF-8") + " from " + r.sender))
  }

}


