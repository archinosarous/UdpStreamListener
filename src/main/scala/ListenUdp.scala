package com.test.udp

import java.net.InetSocketAddress
import akka.actor.{ActorLogging, ActorRef, Props}
import akka.io.{IO, Udp}
import akka.stream.actor.ActorPublisher
import akka.stream.scaladsl.Source
import jdk.nashorn.api.scripting.JSObject
import jdk.nashorn.internal.parser.JSONParser
import play.api.libs.json.Json


object ListenUdp {
  def props(listenOn: InetSocketAddress, maxBufferSize: Int = 100): Props = Props(new ListenUdp(listenOn))
  def apply(listenOn: InetSocketAddress, maxBufferSize: Int = 100) = Source.actorPublisher[Udp.Received](ListenUdp.props(listenOn))
}

class ListenUdp(remote: InetSocketAddress) extends ActorPublisher[Udp.Received] with ActorLogging {
  import context.system

  IO(Udp) ! Udp.Bind(self, remote)

  def receive = {
    case Udp.Bound(local) =>
      log.info(s"############ local: $local")
      context.become(ready(sender()))
  }

  def ready(socket: ActorRef): Receive = {
    case Udp.Received(data, remote) =>
      log.info(s" ############### data: $data")
      log.info(s" ############### data: ${data.decodeString("UTF-8")}")
      log.info(s" ############### data: ${data.decodeString("UTF-8")}")
      log.info(s" ############### remote: $remote")
      val processed = convertToGelf(data.decodeString("UTF-8"))
//      println(s"%%%%%%%%%%%%%% processed: $processed")
        socket ! Udp.Send(data, remote) // example server echoes back
     // nextActor ! processed
    case Udp.Unbind  => socket ! Udp.Unbind
    case Udp.Unbound => context.stop(self)
  }

  private def convertToGelf(gelfString: String): GelfFormat = {
    val jsonObject = Json.parse(gelfString)
    println(s"############### $jsonObject")
    println(s"############### $gelfString")
    val x = jsonObject.as[GelfFormat]
    println(s"############## x: $x")
    x
  }

}