package actors.user
import akka.actor.{Actor, Props}
import akka.util.Timeout
import dao.{StatsDAO, UserDAO}
import models.betting.request.JoinBetRequest
import models.user.User
import models.user.request.BetUserResult

import scala.concurrent.duration._

sealed trait UserActorMessage

object UserActor {

  def props(userDao: UserDAO, statsDao: StatsDAO): Props =
    Props(new UserActor(userDao, statsDao))

  final case class JoinBet(joinBetRequest: JoinBetRequest)
      extends UserActorMessage

  final case class UpdateUser(user: User) extends UserActorMessage

  final case class Login(user: User) extends UserActorMessage

  final case class UpdateBet(betUserResult: BetUserResult)
      extends UserActorMessage

  final case class UpdateWallet(profit: Double) extends UserActorMessage

  final case class Update(updatedUser: User) extends UserActorMessage

  case object UserLogout extends UserActorMessage

  case object GetWallet extends UserActorMessage

  case object UserStats extends UserActorMessage
}

final class UserActor(userDao: UserDAO, statsDao: StatsDAO) extends Actor {

  import UserActor._

  implicit val timeout: Timeout = 5 second

  var myBets: Vector[BetUserResult] = Vector.empty
  var currentUser: User = _

  def receive: Receive = {

    case JoinBet(joinBetRequest: JoinBetRequest) =>
      val updatedUser = User(
        id = currentUser.id,
        password = currentUser.password,
        email = currentUser.email,
        firstName = currentUser.firstName,
        lastName = currentUser.lastName,
        wallet = currentUser.wallet - joinBetRequest.investInScalaCoins
      )
      self ! Update(updatedUser)

    case UpdateBet(betUserResult: BetUserResult) =>
      myBets = myBets :+ betUserResult
      statsDao.addStats(betUserResult)
      self ! UpdateWallet(betUserResult.gain)

    case UpdateWallet(amount: Double) =>
      val updatedUser = currentUser.copy(wallet = currentUser.wallet + amount)

      self ! Update(updatedUser)

    case Update(updatedUser: User) =>
      currentUser = updatedUser
      userDao.updateUser(updatedUser)

    case GetWallet =>
      sender ! currentUser.wallet

    case Login(user: User) =>
      val loggedInUser = User(id = user.id,
                              password = user.password,
                              email = user.email,
                              firstName = user.firstName,
                              lastName = user.lastName,
                              wallet = user.wallet)
      self ! Update(loggedInUser)

    case UserStats =>
      val countBets = myBets.length

      val userStats = Vector(currentUser.id, myBets.length)

  }
}

sealed trait UserActorTag
