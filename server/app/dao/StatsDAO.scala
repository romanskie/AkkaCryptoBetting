package dao

import models.user.request.BetUserResult
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class StatsDAO(dbConfig: DatabaseConfig[JdbcProfile])(
    implicit ec: ExecutionContext) {

  import dbConfig.profile.api._
  val db = dbConfig.db
  val stats = TableQuery[StatTable]

  def getStatsByUserId(userID: Long): Future[Seq[BetUserResult]] = {
    db.run {
      stats.filter(_.userId === userID).result
    }
  }
  def getStatsByBetId(betID: Long): Future[Seq[BetUserResult]] = {
    db.run {
      stats.filter(_.betId === betID).result
    }
  }
  def getAllStats: Future[Seq[BetUserResult]] = {
    db.run {
      stats.result
    }
  }
  def addStats(betUserResult: BetUserResult): Future[BetUserResult] = {
    db.run {
      stats returning stats.map(_.id) into (
          (betUserResult,
           id) => betUserResult.copy(id = Some(id))) += BetUserResult(
        None,
        betUserResult.betID,
        betUserResult.userID,
        betUserResult.invest,
        betUserResult.prediction,
        betUserResult.joinTime,
        betUserResult.gain)
    }
  }

}

class StatTable(tag: Tag) extends Table[BetUserResult](tag, "stats") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def betId = column[Long]("betId")
  def userId = column[Long]("userId")
  def invest = column[Double]("invest")
  def prediction = column[Double]("prediction")
  def joinTime = column[Long]("joinTime")
  def gain = column[Double]("gain")

  override def * =
    (id ?, betId, userId, invest, prediction, joinTime, gain) <> ((BetUserResult.apply _).tupled, BetUserResult.unapply)

}
