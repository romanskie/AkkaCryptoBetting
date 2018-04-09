package models.betting.request


import io.circe._
import io.circe.generic.semiauto._

case class JoinBetRequest(
                           betId: Long,
                           userId: Long,
                           investInScalaCoins: Double,
                           prediction: Double
                         )

object JoinBetRequest {
  implicit val joinBetRequestDecoder: Decoder[JoinBetRequest] = deriveDecoder
  implicit val joinBetRequestEncoder: Encoder[JoinBetRequest] = deriveEncoder
}
