package models.betting

import io.circe.generic.extras.semiauto.{deriveEnumerationDecoder, deriveEnumerationEncoder}
import io.circe.{Decoder, Encoder}

sealed trait BettingTopic
case object BettingTopic {
  case object Bitcoin extends BettingTopic
  case object Ethereum extends BettingTopic
  case object NoTopic extends BettingTopic

  implicit val decodeBettingTopic: Decoder[BettingTopic] =
    deriveEnumerationDecoder

  implicit val encodeBettingTopic: Encoder[BettingTopic] =
    deriveEnumerationEncoder
}
