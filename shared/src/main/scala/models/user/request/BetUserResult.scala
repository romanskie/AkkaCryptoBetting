package models.user.request

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class BetUserResult(
                                id: Option[Long],
                                betID: Long,
                                userID: Long,
                                invest: Double,
                                prediction: Double,
                                joinTime: Long,
                                gain: Double
                              )

object BetUserResult {
  implicit val betUserResultDecoder: Decoder[BetUserResult] = deriveDecoder
  implicit val betUserResultEncoder: Encoder[BetUserResult] = deriveEncoder
}
