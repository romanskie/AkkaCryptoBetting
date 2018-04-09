package models.user.request


import io.circe._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

final case class LogoutUserRequest(id: Option[Long])

object LogoutUserRequest {
  implicit val logoutUserRequestDecoder: Decoder[LogoutUserRequest] = deriveDecoder
  implicit val logoutUserRequestEncoder: Encoder[LogoutUserRequest] = deriveEncoder
}
