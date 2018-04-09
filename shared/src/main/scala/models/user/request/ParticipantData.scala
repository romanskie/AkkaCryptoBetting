package models.user.request

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class ParticipantData(id: Long,
                                 invest:Double,
                                 prediction:Double,
                                 joinTime:Long)

object ParticipantData {
  implicit val participantDataDecoder: Decoder[ParticipantData] = deriveDecoder
  implicit val participantDataEncoder: Encoder[ParticipantData] = deriveEncoder
}
