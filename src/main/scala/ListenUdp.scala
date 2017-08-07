import java.net.InetSocketAddress
import akka.actor.{ActorLogging, ActorRef, Props}
import akka.io.{IO, Udp}
import akka.stream.actor.ActorPublisher
import akka.stream.scaladsl.Source

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
      log.info(s" ############### remote: $remote")
     // val processed = // parse data etc., e.g. using PipelineStage
        socket ! Udp.Send(data, remote) // example server echoes back
     // nextActor ! processed
    case Udp.Unbind  => socket ! Udp.Unbind
    case Udp.Unbound => context.stop(self)
  }
}