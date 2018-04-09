package models.betting.request

import io.circe._
import io.circe.generic.extras.semiauto._
import io.circe.syntax._
import models.betting.{BettingCategory, BettingTopic}

final case class CreateBetRequest(
                                   userId: Long,
                                   duration: Long,
                                   investInScalaCoins: Double,
                                   prediction: Double,
                                   bettingCategory: BettingCategory,
                                   bettingTopic: BettingTopic
)

object CreateBetRequest {

  implicit val decodeBettingTopic: Decoder[BettingTopic] =
    deriveEnumerationDecoder
  implicit val encodeBettingTopic: Encoder[BettingTopic] =
    deriveEnumerationEncoder
  implicit val decodeBettingCategory: Decoder[BettingCategory] =
    deriveEnumerationDecoder
  implicit val encodeBettingCategory: Encoder[BettingCategory] =
    deriveEnumerationEncoder

  implicit val encodeCreateBet: Encoder[CreateBetRequest] = Encoder.instance {
    case CreateBetRequest(userId, duration, investInScalaCoins, prediction, bettingCategory, bettingTopic) =>
      Json.obj(
        "userId" -> userId.asJson,
        "duration" -> duration.asJson,
        "investInScalaCoins" -> investInScalaCoins.asJson,
        "prediction" -> prediction.asJson,
        "bettingCategory" -> bettingCategory.asJson,
        "bettingTopic" -> bettingTopic.asJson
      )
  }

  implicit val decodeCreateBet: Decoder[CreateBetRequest] =
    new Decoder[CreateBetRequest] {
      final def apply(c: HCursor): Decoder.Result[CreateBetRequest] =
        for {
          userId <- c.downField("userId").as[Long]
          duration <- c.downField("duration").as[Long]
          investInScalaCoins <- c.downField("investInScalaCoins").as[Double]
          prediction <- c.downField("prediction").as[Double]
          bettingCategory <- c.downField("bettingCategory").as[BettingCategory]
          bettingTopic <- c.downField("bettingTopic").as[BettingTopic]
        } yield {
          new CreateBetRequest(
            userId,
            duration,
            investInScalaCoins,
            prediction,
            bettingCategory,
            bettingTopic
          )
        }
    }

}
