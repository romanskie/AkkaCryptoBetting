package controllers

import java.net.URI

import actors.crypto.{
  CryptoPriceFetchActorTag,
  CryptoPriceWebsocketHandlerActor
}
import akka.actor.{ActorRef, ActorSystem}
import akka.stream.Materializer
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub, Source}
import akka.util.Timeout
import com.softwaremill.tagging.@@
import play.api.Logger
import play.api.libs.circe.Circe
import play.api.libs.streams.ActorFlow
import play.api.mvc.{
  AbstractController,
  ControllerComponents,
  RequestHeader,
  WebSocket
}

import scala.concurrent.{ExecutionContext, Future}

class SocketController(
    cc: ControllerComponents,
    cryptoPriceFetchActor: ActorRef @@ CryptoPriceFetchActorTag
)(implicit ec: ExecutionContext,
  actorSystem: ActorSystem,
  materializer: Materializer,
  timeout: Timeout)
    extends AbstractController(cc)
    with Circe {

  private type WSMessage = String
  private val logger = Logger(getClass)

  def cryptoPrice = WebSocket.accept[WSMessage, WSMessage] { implicit request =>
    ActorFlow.actorRef { out =>
      CryptoPriceWebsocketHandlerActor.props(cryptoPriceFetchActor, out)
    }
  }

  private val (chatSink, chatSource) = {
    val source = MergeHub
      .source[WSMessage]
      .log("source")
      .recoverWithRetries(-1, { case _: Exception ⇒ Source.empty })

    val sink = BroadcastHub.sink[WSMessage]
    source.toMat(sink)(Keep.both).run()
  }

  private val userFlow: Flow[WSMessage, WSMessage, _] = {
    Flow.fromSinkAndSource(chatSink, chatSource)
  }

  def chat(): WebSocket = {
    WebSocket.acceptOrResult[WSMessage, WSMessage] {
      case rh if sameOriginCheck(rh) =>
        Future
          .successful(userFlow)
          .map { flow =>
            Right(flow)
          }
          .recover {
            case e: Exception =>
              val msg = "Cannot create websocket"
              logger.error(msg, e)
              val result = InternalServerError(msg)
              Left(result)
          }

      case rejected =>
        logger.error(s"Request ${rejected} failed same origin check")
        Future.successful {
          Left(Forbidden("forbidden"))
        }
    }
  }

  private def sameOriginCheck(implicit rh: RequestHeader): Boolean = {
    // The Origin header is the domain the request originates from.
    // https://tools.ietf.org/html/rfc6454#section-7
    logger.debug("Checking the ORIGIN ")

    rh.headers.get("Origin") match {
      case Some(originValue) if originMatches(originValue) =>
        logger.debug(s"originCheck: originValue = $originValue")
        true

      case Some(badOrigin) =>
        logger.error(
          s"originCheck: rejecting request because Origin header value ${badOrigin} is not in the same origin")
        false

      case None =>
        logger.error(
          "originCheck: rejecting request because no Origin header found")
        false
    }
  }

  private def originMatches(origin: String): Boolean = {
    try {
      val url = new URI(origin)
      url.getHost == "localhost" &&
      (url.getPort match { case 9000 | 19001 => true; case _ => false })
    } catch {
      case e: Exception => false
    }
  }

  def socket = WebSocket.accept[WSMessage, WSMessage] { implicit request =>
    // log the message to stdout and send response back to client
    Flow[WSMessage].map { msg =>
      println(msg)
      "I received your message: " + msg
    }
  }

}
