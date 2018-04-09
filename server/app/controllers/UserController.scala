package controllers

import actors.user.UserManagerActor._
import actors.user.UserManagerActorTag
import akka.actor.ActorRef
import akka.util.Timeout
import com.softwaremill.tagging.@@
import dao.{StatsDAO, UserDAO}
import io.circe.syntax._
import models.user.request.LoginUserRequest
import play.api.libs.circe.Circe
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

final class UserController(userManagerActor: ActorRef @@ UserManagerActorTag,
                           cc: ControllerComponents,
                           userDao: UserDAO,
                           statsDao: StatsDAO)(implicit ec: ExecutionContext)
    extends AbstractController(cc)
    with Circe {

  implicit val timeout = Timeout(5 seconds)

  def getUsers = Action.async { implicit request =>
    userDao.getUsers map {
      case Seq() => InternalServerError("Currently no users exist".asJson)
      case users => Ok(users.asJson)
    }
  }

  def getUserByEmail(email: String) = Action.async {
    userDao.getUser(email) map {
      case Some(user) => Ok(user.asJson)
      case None       => InternalServerError("No user for email " + email.asJson)
    }
  }

  def getBetStatsByBetId(betId: Long) = Action.async {
    statsDao.getStatsByBetId(betId) map {
      case Seq() =>
        InternalServerError("No bet stats exist for betId " + betId.asJson)
      case stats => Ok(stats.asJson)
    }
  }

  def getBetStatsByUserId(userId: Long) = Action.async {
    statsDao.getStatsByUserId(userId) map {
      case Seq() =>
        InternalServerError("No bet stats exist for userId " + userId.asJson)
      case stats => Ok(stats.asJson)
    }
  }

  def getAllBetStats = Action.async { implicit request =>
    statsDao.getAllStats map { stats =>
      Ok(stats.asJson)
    }
  }

  def logout = Action { implicit request =>
    Ok("You are loggend out".asJson)
  }

  def login = Action.async(circe.json[LoginUserRequest]) { implicit request =>
    val userRequest = request.body
    userDao.getUser(userRequest.email) map {
      case Some(user) =>
        userManagerActor ! CreateUserActor(user)
        Ok(user.asJson)
      case None =>
        println("no user found")
        Unauthorized("Login Failed")
    }

  }
}
