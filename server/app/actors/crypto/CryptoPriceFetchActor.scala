package actors.crypto

import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import models.betting.BettingTopic
import models.crypto.{CryptoCompareResponse, CryptoPrice}
import play.api.libs.ws.ahc.AhcWSClient
import utils.CirceJsonBodyReadables

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success}

sealed trait CryptoPriceFetchActorTag
sealed trait CryptoPriceFetchActorMessages

object CryptoPriceFetchActor {

  def props: Props = Props(new CryptoPriceFetchActor)

  val btcURL =
    "https://min-api.cryptocompare.com/data/price?fsym=BTC&tsyms=ETH,USD,EUR"

  val scalaCoinIndex = 1.65

  case object Fetch extends CryptoPriceFetchActorMessages

  final case class GetPrice(bettingTopic: BettingTopic)
      extends CryptoPriceFetchActorMessages

  def euroToScalaCoins(euro: Double, scalaCoinIndex: Double): Double =
    euro * scalaCoinIndex

  def calculateEtherPrice(
      cryptoCompareResponse: CryptoCompareResponse): CryptoPrice = {

    val btcInEuro = cryptoCompareResponse.EUR
    val btcInEther = cryptoCompareResponse.ETH
    val btcInUSD = cryptoCompareResponse.USD

    val etherInEuro = btcInEuro / btcInEther
    val etherInBTC = 1.0 / btcInEther
    val etherInUSD = btcInUSD / btcInEther

    CryptoPrice(
      name = "ether",
      priceInEther = 1.0,
      priceInBTC = etherInBTC,
      priceInEuro = etherInEuro,
      priceInUSD = etherInUSD,
      priceInScalaCoins = euroToScalaCoins(etherInEuro, scalaCoinIndex)
    )
  }

}

final class CryptoPriceFetchActor extends Actor with CirceJsonBodyReadables {

  import CryptoPriceFetchActor._

  implicit val timeout: Timeout = 5 seconds
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val mat: Materializer = ActorMaterializer()

  context.system.scheduler.schedule(0.seconds, 15.seconds, self, Fetch)
  var currentBTC = CryptoPrice(name = "bitcoin")
  var currentETH = CryptoPrice(name = "ether")

  override def receive: Receive = {
    case Fetch =>
      val ws = AhcWSClient()
      ws.url(btcURL)
        .get()
        .map(response => response.body[io.circe.Json].as[CryptoCompareResponse])
        .onComplete {
          case Success(response) =>
            response match {
              case Left(decodingFailure) => throw decodingFailure
              case Right(value) =>
                currentBTC = CryptoPrice(
                  name = "bitcoin",
                  priceInBTC = 1D,
                  priceInEther = value.ETH,
                  priceInEuro = value.EUR,
                  priceInUSD = value.USD,
                  priceInScalaCoins =
                    euroToScalaCoins(value.EUR, scalaCoinIndex)
                )
                currentETH = calculateEtherPrice(value)
                ws.close()
            }
          case Failure(msg) =>
            println("An error has occured: " + msg.getMessage)
            ws.close()
        }

    case GetPrice(topic: BettingTopic) =>
      topic match {
        case BettingTopic.Bitcoin  => sender ! currentBTC
        case BettingTopic.Ethereum => sender ! currentETH
      }
  }
}
