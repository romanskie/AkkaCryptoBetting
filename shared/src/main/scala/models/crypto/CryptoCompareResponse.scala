package models.crypto

import io.circe._
import io.circe.syntax._

final case class CryptoCompareResponse(
    ETH: Double,
    USD: Double,
    EUR: Double
)

object CryptoCompareResponse {

  implicit val encodeCryptoCompareResponse: Encoder[CryptoCompareResponse] =
    Encoder.instance {
      case CryptoCompareResponse(eth, usd, eur) =>
        Json.obj(
          "ETH" -> eth.asJson,
          "USD" -> usd.asJson,
          "EUR" -> eur.asJson
        )
    }

  implicit val decodeCryptoCompareResponse: Decoder[CryptoCompareResponse] =
    new Decoder[CryptoCompareResponse] {
      final def apply(c: HCursor): Decoder.Result[CryptoCompareResponse] =
        for {
          eth <- c.downField("ETH").as[Double]
          usd <- c.downField("USD").as[Double]
          eur <- c.downField("EUR").as[Double]
        } yield {
          CryptoCompareResponse(eth, usd, eur)
        }
    }
}
