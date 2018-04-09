package utils

import play.api.libs.ws.BodyReadable

trait CirceJsonBodyReadables {

  implicit val circeJsonBodyReadable = BodyReadable[io.circe.Json] { response =>
    io.circe.parser.parse(response.bodyAsBytes.utf8String) match {
      case Left(decodingFailure) => throw decodingFailure
      case Right(json)           => json
    }
  }

}
