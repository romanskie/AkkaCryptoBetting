package utils

import akka.util.ByteString
import play.api.libs.ws.{BodyWritable, InMemoryBody}

trait CirceJsonBodyWritables {

  implicit val bodyWritableOf_Json: BodyWritable[io.circe.Json] = {
    BodyWritable(
      json => InMemoryBody(ByteString.fromString(json.noSpaces)),
      "application/json"
    )
  }

}
