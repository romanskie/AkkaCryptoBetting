package modules

import com.softwaremill.macwire._
import dao.{BetDAO, StatsDAO, UserDAO}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

trait DaoModule {
  implicit def ec: ExecutionContext

  def dbConfig: DatabaseConfig[JdbcProfile]

  lazy val statsDao: StatsDAO = wire[StatsDAO]
  lazy val betDao: BetDAO = wire[BetDAO]
  lazy val userDao: UserDAO = wire[UserDAO]
}
