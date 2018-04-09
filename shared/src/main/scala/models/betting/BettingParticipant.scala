package models.betting

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class BettingParticipant(
                                 userId: Long,
                                 joinTime: Long,
                                 invest: Double,
                                 prediction:Double,
                                 currentPrice: Double
                               )

object BettingParticipant {
  implicit val userBetStateDecoder: Decoder[BettingParticipant] = deriveDecoder
  implicit val userBetStateEncoder: Encoder[BettingParticipant] = deriveEncoder
}

