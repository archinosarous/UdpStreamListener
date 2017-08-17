package io.skul



import java.net.InetSocketAddress

import akka.actor.{ActorLogging, ActorRef, Props}
import akka.io.{IO, Udp}
import akka.stream.actor.ActorPublisher
import akka.stream.scaladsl.Source
import play.api.libs.json.{JsNumber, JsObject, JsString, Json}

import scala.util.{Failure, Success, Try}


object ListenUdp {
  def props(listenOn: InetSocketAddress, maxBufferSize: Int = 100): Props = Props(new ListenUdp(listenOn))
  def apply(listenOn: InetSocketAddress, maxBufferSize: Int = 100): Source[GelfMessage, ActorRef] =
    Source.actorPublisher[GelfMessage](ListenUdp.props(listenOn))
}

class ListenUdp(remote: InetSocketAddress) extends ActorPublisher[GelfMessage] with ActorLogging {
  import context.system

  IO(Udp) ! Udp.Bind(self, remote)

  def receive :Receive = {
    case Udp.Bound(local) =>
      log.info(s"############ local: $local")
      context.become(ready(sender()))
  }

  def ready(socket: ActorRef): Receive = {
    case Udp.Received(data, _) =>
      convertToGelf(data.decodeString("UTF-8")) match {
        case Success(message) => onNext(message)
        case Failure(ex0) => ex0.printStackTrace()
      }

    case Udp.Unbind  => socket ! Udp.Unbind
    case Udp.Unbound => context.stop(self)

  }

  private def convertToGelf(gelfString: String): Try[GelfMessage]= Try {

    val jsonObject = Json.parse(gelfString).as[JsObject]
    val version: String = jsonObject.value("version").as[JsString].value
    val host: String = jsonObject.value("host").as[JsString].value
    val shortMessage: String = jsonObject.value("short_message").as[JsString].value
    val fullMessage: Option[String] = jsonObject.value.get("fullMessage").map(_.as[JsString].value)
    val timestamp: Option[Double] = jsonObject.value.get("timestamp").map(_.as[JsNumber].value.toDouble)
    val level: Int = jsonObject.value("level").as[JsNumber].value.toInt

    val list : List[OptionalField[_]] = jsonObject.keys.filter(_.startsWith("_")).map{ key =>
      jsonObject.value(key) match {
        case JsString(value) => StringOptionalField(key , value)
        case JsNumber(value) => DoubleOptionalField(key , value.toDouble)
      }
    }.toList

    GelfMessage(version , host , shortMessage, fullMessage, timestamp, level ,list)
  }

}