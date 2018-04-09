package dao

import models.user.User
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class UserDAO(dbConfig: DatabaseConfig[JdbcProfile])(
    implicit ec: ExecutionContext) {

  import dbConfig.profile.api._

  val db = dbConfig.db
  val users = TableQuery[UserTable]

  def getUsers: Future[Seq[User]] = {
    db.run {
      users.result
    }
  }

  def getUser(thisEmail: String): Future[Option[User]] = {
    db.run(
      users
        .filter(_.email.toLowerCase === thisEmail.toLowerCase)
        .result
        .headOption)
  }

  def updateUser(user: User): Future[Unit] = {
    val userID = user.id
    val userToUpdate: User = user.copy(userID)
    db.run(users.filter(_.id === user.id).update(userToUpdate)).map(_ => ())
  }
}

class UserTable(tag: Tag) extends Table[User](tag, "user") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def password = column[String]("password")
  def email = column[String]("email")
  def firstName = column[String]("firstName")
  def lastName = column[String]("lastName")
  def wallet = column[Double]("wallet")

  override def * =
    (id ?, password, email, firstName, lastName, wallet) <> ((User.apply _).tupled, User.unapply)

}
