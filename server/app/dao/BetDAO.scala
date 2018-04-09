package dao

import models.betting.BettingCategory.{Closed, Open}
import models.betting.BettingTopic.{Bitcoin, Ethereum, NoTopic}
import models.betting.{Bet, BettingCategory, BettingTopic}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

final class BetDAO(dbConfig: DatabaseConfig[JdbcProfile])(
    implicit ec: ExecutionContext) {

  import dbConfig.profile.api._

  val db = dbConfig.db
  val bets = TableQuery[BetTable]

  def getBetById(id: Long): Future[Option[Bet]] = {
    db.run(bets.filter(_.id === id).result.headOption)
  }

  def getBets: Future[Seq[Bet]] = {
    db.run {
      bets.result
    }
  }

  def getClosedBets: Future[Seq[Bet]] = {
    db.run {
      bets.filter(_.running === false).result
    }
  }

  def getRunningBets: Future[Seq[Bet]] = {
    db.run {
      bets.filter(_.running === true).result
    }
  }

  def updateBet(bet: Bet): Future[Unit] = {
    val betId = bet.id
    val betToUpdate: Bet = bet.copy(betId)
    db.run {
      bets.filter(_.id === bet.id).update(betToUpdate).map(_ => ())
    }
  }

  def createBet(bet: Bet): Future[Bet] = {
    db.run {
      (bets returning bets.map(_.id) into ((bet, id) => bet.copy(id = Some(id)))) += Bet(
        None,
        bet.startTime,
        bet.stopTime,
        bet.duration,
        bet.bettingTopic,
        bet.bettingCategory,
        bet.startPriceInScalaCoins,
        bet.endPriceInScalaCoins,
        bet.prediction,
        bet.poolInScalaCoins,
        bet.participants,
        bet.running
      )
    }
  }

}

final class BetTable(tag: Tag) extends Table[Bet](tag, "bet") {

  private def bettingCategoryToString(b: BettingCategory): String = b match {
    case Open   => "Open"
    case Closed => "Closed"
  }

  private def stringToBettingCategory(s: String): BettingCategory = s match {
    case "Open"   => Open
    case "Closed" => Closed
  }

  private def bettingTopicToString(b: BettingTopic): String = b match {
    case Bitcoin  => "Bitcoin"
    case Ethereum => "Ethereum"
    case NoTopic  => "Notopic"
  }

  private def stringToBettingTopic(s: String): BettingTopic = s match {
    case "Bitcoin"  => Bitcoin
    case "Ethereum" => Ethereum
    case "Notopic"  => NoTopic

  }

  implicit val categoryColumnType =
    MappedColumnType.base[BettingCategory, String](
      b => {
        bettingCategoryToString(b)
      },
      s => {
        stringToBettingCategory(s)
      }
    )

  implicit val topicColumnType = MappedColumnType.base[BettingTopic, String](
    b => {
      bettingTopicToString(b)
    },
    s => {
      stringToBettingTopic(s)
    }
  )

  implicit val vectorLongColumnType =
    MappedColumnType.base[Vector[Long], String](
      vector => {
        vector.mkString(",")
      },
      str => {
        str
          .split(",")
          .map(s => s.toLong)
          .toVector
      }
    )
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def startTime = column[Long]("startTime")
  def stopTime = column[Long]("stopTime")
  def duration = column[Long]("duration")
  def bettingTopic = column[BettingTopic]("bettingTopic")
  def bettingCategory = column[BettingCategory]("bettingCategory")
  def startPriceInScalaCoins = column[Double]("startPriceInScalaCoins")
  def endPriceInScalaCoins = column[Double]("endPriceInScalaCoins")
  def prediction = column[Double]("prediction")
  def poolInScalaCoins = column[Double]("poolInScalaCoins")
  def participants = column[Vector[Long]]("participants")
  def running = column[Boolean]("running")

  override def * =
    (id ?,
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
     running) <> ((Bet.apply _).tupled, Bet.unapply)

}
