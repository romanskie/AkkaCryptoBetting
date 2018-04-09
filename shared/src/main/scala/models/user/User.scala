package models.user

import io.circe.syntax._
import io.circe.{Decoder, Encoder, HCursor, Json}


final case class User(
    id: Option[Long],
    password: String,
    email: String,
    firstName: String,
    lastName: String,
    wallet: Double
)
object User {
  implicit val userDecoder: Decoder[User] = new Decoder[User] {
    final def apply(c: HCursor): Decoder.Result[User] = for {
      id <- c.downField("id").as[Option[Long]]
      password <- c.downField("password").as[String]
      email <- c.downField("email").as[String]
      firstName <- c.downField("firstName").as[String]
      lastName <- c.downField("lastName").as[String]
      wallet <- c.downField("wallet").as[Double]
    } yield {
      new User(id, password, email, firstName, lastName, wallet)
    }
  }
  implicit val userEncoder: Encoder[User] = Encoder.instance {
    case User(id, password, email, firstName, lastName, wallet) =>
      Json.obj(
        "id" -> id.asJson,
        "password" -> password.asJson,
        "email" -> email.asJson,
        "firstName" -> firstName.asJson,
        "lastName" -> lastName.asJson,
        "wallet" -> wallet.asJson
      )
  }
}
