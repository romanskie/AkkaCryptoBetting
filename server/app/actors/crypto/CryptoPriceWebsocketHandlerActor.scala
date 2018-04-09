package actors.crypto

import actors.crypto.CryptoPriceFetchActor.GetPrice
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.pattern.{ask, pipe}
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import com.softwaremill.tagging.@@
import io.circe.syntax._
import models.betting.BettingTopic
import models.crypto.CryptoPrice

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

sealed trait CryptoPriceWebsocketHandlerActorMessages

object CryptoPriceWebsocketHandlerActor {

  def props(cryptoPriceFetchActor: ActorRef @@ CryptoPriceFetchActorTag,
            out: ActorRef): Props =
    Props(new CryptoPriceWebsocketHandlerActor(cryptoPriceFetchActor, out))

  case object AskCryptoPriceFetchActor
      extends CryptoPriceWebsocketHandlerActorMessages

  case object Fetch extends CryptoPriceWebsocketHandlerActorMessages

  final case class SendPrice(priceTuple: (CryptoPrice, CryptoPrice))
      extends CryptoPriceWebsocketHandlerActorMessages
}

final class CryptoPriceWebsocketHandlerActor(
    cryptoPriceFetchActor: ActorRef @@ CryptoPriceFetchActorTag,
    out: ActorRef)
    extends Actor
    with ActorLogging {

  import CryptoPriceWebsocketHandlerActor._

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val mat: Materializer = ActorMaterializer()
  implicit val timeout: Timeout = 5 seconds

  context.system.scheduler.schedule(0.seconds, 10.seconds, self, Fetch)

  def receive: Receive = {

    case Fetch =>
      val f1 = (cryptoPriceFetchActor ? GetPrice(BettingTopic.Bitcoin))
        .mapTo[CryptoPrice]
      val f2 = (cryptoPriceFetchActor ? GetPrice(BettingTopic.Ethereum))
        .mapTo[CryptoPrice]
      val result = for { v1 <- f1; v2 <- f2 } yield (v1, v2)
      result.map(SendPrice).pipeTo(self)

    case SendPrice(priceAsJsonString: (CryptoPrice, CryptoPrice)) =>
      out ! priceAsJsonString._1.asJson.noSpaces
      out ! priceAsJsonString._2.asJson.noSpaces
  }

}
