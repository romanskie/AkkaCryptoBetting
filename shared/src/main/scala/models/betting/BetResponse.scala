package models.betting

import io.circe._
import io.circe.generic.semiauto._

case class BetResponse(
    betId: Option[Long],
    startTime: Option[Long],
    stopTime: Option[Long],
    currentTime: Option[Long],
    currencyRate: Option[Double]
)

object BetResponse {
  implicit val betRequestDecoder: Decoder[BetResponse] = deriveDecoder
  implicit val betRequestEncoder: Encoder[BetResponse] = deriveEncoder
}
