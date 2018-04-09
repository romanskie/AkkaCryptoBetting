package models.betting

import io.circe._
import io.circe.generic.extras.semiauto._
import io.circe.syntax._

final case class Bet(
                      id: Option[Long],
                      startTime: Long,
                      stopTime: Long,
                      duration: Long,
                      bettingTopic: BettingTopic,
                      bettingCategory: BettingCategory,
                      startPriceInScalaCoins: Double,
                      endPriceInScalaCoins: Double,
                      prediction: Double,
                      poolInScalaCoins: Double,
                      participants: Vector[Long],
                      running: Boolean,
                    )

object Bet {

  implicit val decodeBettingTopic: Decoder[BettingTopic] =
    deriveEnumerationDecoder
  implicit val encodeBettingTopic: Encoder[BettingTopic] =
    deriveEnumerationEncoder

  implicit val decodeBettingCategory: Decoder[BettingCategory] =
    deriveEnumerationDecoder
  implicit val encodeBettingCategory: Encoder[BettingCategory] =
    deriveEnumerationEncoder

  implicit val encodeBet: Encoder[Bet] = Encoder.instance {
    case Bet(id,
    startTime,
    stopTime,
    duration,
    bettingTopic,
    bettingCategory,
    startPriceInScalaCoins,
    endPriceInScalaCoins,
    prediction,
    poolInScalaCoins,
    participants,
    running) =>
      Json.obj(
        "id" -> id.asJson,
        "startTime" -> startTime.asJson,
        "stopTime" -> stopTime.asJson,
        "duration" -> duration.asJson,
        "bettingTopic" -> bettingTopic.asJson,
        "bettingCategory" -> bettingCategory.asJson,
        "startPriceInScalaCoins" -> startPriceInScalaCoins.asJson,
        "endPriceInScalaCoins" -> endPriceInScalaCoins.asJson,
        "prediction" -> prediction.asJson,
        "poolInScalaCoins" -> poolInScalaCoins.asJson,
        "participants" -> participants.asJson,
        "running" -> running.asJson
      )
  }

  implicit val decodeBet: Decoder[Bet] = new Decoder[Bet] {
    final def apply(c: HCursor): Decoder.Result[Bet] =
      for {
        id <- c.downField("id").as[Option[Long]]
        startTime <- c.downField("startTime").as[Long]
        stopTime <- c.downField("stopTime").as[Long]
        duration <- c.downField("duration").as[Long]
        bettingTopic <- c.downField("bettingTopic").as[BettingTopic]
        bettingCategory <- c.downField("bettingCategory").as[BettingCategory]
        startPriceInScalaCoins <- c
          .downField("startPriceInScalaCoins")
          .as[Double]
        endPriceInScalaCoins <- c.downField("endPriceInScalaCoins").as[Double]
        prediction <- c.downField("prediction").as[Double]
        poolInScalaCoins <- c
          .downField("poolInScalaCoins")
          .as[Double]
        participants <- c.downField("participants").as[Vector[Long]]
        running <- c.downField("running").as[Boolean]
      } yield {
        new Bet(id,
          startTime,
          stopTime,
          duration,
          bettingTopic,
          bettingCategory,
          startPriceInScalaCoins,
          endPriceInScalaCoins,
          prediction,
          poolInScalaCoins,
          participants,
          running)
      }
  }

}
