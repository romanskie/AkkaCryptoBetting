package models.user.request


import io.circe._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

final case class LoginUserRequest(email: String, password: String)

object LoginUserRequest {
    implicit val loginUserRequestDecoder: Decoder[LoginUserRequest] = deriveDecoder
    implicit val loginUserRequestEncoder: Encoder[LoginUserRequest] = deriveEncoder
}
