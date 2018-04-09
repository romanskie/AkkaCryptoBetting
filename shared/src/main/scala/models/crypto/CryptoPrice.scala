package models.crypto

import io.circe._
import io.circe.syntax._

final case class CryptoPrice(
    name: String,
    priceInEther: Double = 0,
    priceInBTC: Double = 0,
    priceInEuro: Double = 0,
    priceInUSD: Double = 0,
    priceInScalaCoins: Double = 0
)

object CryptoPrice {

  implicit val encodeCryptoPrice: Encoder[CryptoPrice] = Encoder.instance {
    case CryptoPrice(name,
                     priceInEther,
                     priceInBTC,
                     priceInEuro,
                     priceInUSD,
                     priceInScalaCoins) =>
      Json.obj(
        "name" -> name.asJson,
        "priceInEther" -> priceInEther.asJson,
        "priceInBTC" -> priceInBTC.asJson,
        "priceInEuro" -> priceInEuro.asJson,
        "priceInUSD" -> priceInUSD.asJson,
        "priceInScalaCoins" -> priceInScalaCoins.asJson
      )
  }

  implicit val decodeCryptoPrice: Decoder[CryptoPrice] =
    new Decoder[CryptoPrice] {
      final def apply(c: HCursor): Decoder.Result[CryptoPrice] =
        for {
          name <- c.downField("name").as[String]
          priceInEther <- c.downField("priceInEther").as[Double]
          priceInBTC <- c.downField("priceInBTC").as[Double]
          priceInEuro <- c.downField("priceInEuro").as[Double]
          priceInUSD <- c.downField("priceInUSD").as[Double]
          priceInScalaCoins <- c.downField("priceInScalaCoins").as[Double]
        } yield {
          CryptoPrice(name = name,
            priceInEther = priceInEther,
            priceInBTC = priceInBTC,
            priceInEuro = priceInEuro,
            priceInUSD = priceInUSD,
            priceInScalaCoins = priceInScalaCoins)
        }
    }
}
