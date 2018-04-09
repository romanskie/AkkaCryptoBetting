package actors.user

import actors.user.UserManagerActor.{UserReference, _}
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import dao.{StatsDAO, UserDAO}
import models.betting.request.JoinBetRequest
import models.user.User
import models.user.request.{BetUserResult, LogoutUserRequest}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

sealed trait UserManagerActorMessage

object UserManagerActor {

  def props(userDao: UserDAO, statsDAO: StatsDAO): Props =
    Props(new UserManagerActor(userDao, statsDAO))

  type UserReference = (Option[Long], String)

  case object GetUserChildren extends UserManagerActorMessage

  final case class CreateUserActor(user: User) extends UserManagerActorMessage

  final case class terminateUserActor(user: User)
      extends UserManagerActorMessage

  final case class Login(user: User) extends UserManagerActorMessage

  final case class Logout(logoutUserRequest: LogoutUserRequest)
      extends UserManagerActorMessage

  final case class DistributeProfit(profitVector: Vector[BetUserResult])
      extends UserManagerActorMessage

  final case class JoinUserToBet(joinBetRequest: JoinBetRequest)
      extends UserManagerActorMessage

  final case class SendProfit(managingUsers: Option[UserReference],
                              betUserResult: BetUserResult)
      extends UserManagerActorMessage

  final case class UpdateUserList(managingUsers: UserReference)
      extends UserManagerActorMessage

  final case class ValidateUserInvest(userId: Long,
                                      invest: Double,
                                      wallet: Double)
      extends UserManagerActorMessage

  final case class UserJoinable(userId: Long, invest: Double)
      extends UserManagerActorMessage

  final case class UpdateUserWallet(userId: Long, amount: Double)
      extends UserManagerActorMessage

}

final class UserManagerActor(userDao: UserDAO, statsDAO: StatsDAO)
    extends Actor
    with ActorLogging {

  import UserActor._
  import akka.pattern.{ask, pipe}
  import com.softwaremill.macwire.akkasupport._

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val mat: Materializer = ActorMaterializer()
  implicit val timeout: Timeout = 5 seconds

  var userList: Vector[UserReference] = Vector.empty

  def findRunningUserActor(
      id: Long,
      userVector: Vector[UserReference]): Option[UserReference] = {
    userVector find { _._1.getOrElse(-1) == id }
  }

  override def preStart(): Unit = {
    userDao.getUsers map { users =>
      users foreach (user => self ! CreateUserActor(user))
    }
  }

  def receive: Receive = {

    case UpdateUserWallet(userId: Long, amount: Double) =>
      findRunningUserActor(userId, userList) match {
        case Some(userActor) =>
          context.child(userActor._2) match {
            case Some(userActorChild) => userActorChild ! UpdateWallet(amount)
            case None =>
              log.error("no child for this User actorRef",
                        userActor._2,
                        " available")
          }
      }

    case ValidateUserInvest(userId: Long, invest: Double, wallet: Double) =>
      if (wallet >= invest) {
        self ! UpdateUserWallet(userId, -invest)
        sender ! true
      } else sender ! false

    case UserJoinable(userId: Long, invest: Double) =>
      findRunningUserActor(userId, userList) match {
        case Some(userActor) =>
          context.child(userActor._2) match {
            case Some(userActorChild) =>
              (userActorChild ? GetWallet)
                .mapTo[Double]
                .map(wallet => ValidateUserInvest.apply(userId, invest, wallet))
                .pipeTo(self)(sender)

            case None =>
              log.error("no child for this User actorRef",
                        userActor._2,
                        " available")
          }
      }

    case JoinUserToBet(joinBetRequest: JoinBetRequest) =>
      findRunningUserActor(joinBetRequest.userId, userList) match {
        case Some(userActor) =>
          context.child(userActor._2) match {
            case Some(userActorChild) =>
              userActorChild ! JoinBet(joinBetRequest)
            case None =>
              log.error("no child for this User actorRef",
                        userActor._2,
                        " available")
          }
      }

    case DistributeProfit(profitVector: Vector[BetUserResult]) =>
      val actorPathAndProfitList = profitVector.map(betResult => {
        (findRunningUserActor(betResult.userID, userList), betResult) //Option [ManaingUsers], invest
      })

      actorPathAndProfitList foreach (item => {
        self ! SendProfit(item._1, item._2)
      })

    case SendProfit(managingUsers: Option[UserReference],
                    betUserResult: BetUserResult) =>
      managingUsers match {
        case Some(mUs) =>
          context.child(mUs._2) match {
            case Some(child) => child ! UpdateBet(betUserResult)
            case None =>
              log.error("cant send profit to user actor - no such user actor")
          }
      }
    case None => log.error("cant send profit to user actor - no such user Id")

    case GetUserChildren =>
      sender ! context.children.toVector
      println(userList)

    case UpdateUserList(managingUsers: UserReference) =>
      userList = userList :+ managingUsers

    case CreateUserActor(user: User) =>
      val userActorRef: ActorRef = context.actorOf(wireProps[UserActor])
      userActorRef ! Login(user)
      self ! UpdateUserList((user.id, userActorRef.path.name))

  }
}

sealed trait UserManagerActorTag
