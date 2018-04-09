package actors.betting

import actors.betting.BettingManagerActor._
import actors.betting.BettingTimerActor._
import actors.crypto.CryptoPriceFetchActorTag
import actors.user.UserManagerActor.DistributeProfit
import actors.user.UserManagerActorTag
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import com.softwaremill.tagging.@@
import dao.BetDAO
import models.betting._
import models.betting.request.{CreateBetRequest, JoinBetRequest}
import models.user.request.BetUserResult

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

sealed trait BettingManagerActorTag
sealed trait BettingManagerActorMessage

object BettingManagerActor {

  //betId, bettingActorPathName
  type ManagingBet = (Long, String)
  type ParticipantsPerBet = (Long, Vector[Long])

  def props(betDAO: BetDAO,
            cryptoPriceFetchActor: ActorRef @@ CryptoPriceFetchActorTag,
            userManagerActor: ActorRef @@ UserManagerActorTag): Props =
    Props(
      new BettingManagerActor(betDAO, cryptoPriceFetchActor, userManagerActor))

  final case class CreateBet(createBetRequest: CreateBetRequest)
      extends BettingManagerActorMessage

  final case class UpdateBettingManagerState(bet: Bet, betActorPathName: String)
      extends BettingManagerActorMessage

  final case class TerminateBet(bettingActor: ActorRef)
      extends BettingManagerActorMessage

  final case class GetBetState(bettingId: Long)
      extends BettingManagerActorMessage

  final case class JoinUserToBet(joinBetRequest: JoinBetRequest)
      extends BettingManagerActorMessage

  final case class GetRunningBetsByUserId(userId: Long)
      extends BettingManagerActorMessage

  final case class GetRunningBetById(bettingId: Long)
      extends BettingManagerActorMessage

  final case class GetBet(bettingId: Long) extends BettingManagerActorMessage

  final case class FinishBet(actorRef: ActorRef)
      extends BettingManagerActorMessage

  final case class GetBetJoinState(betId: Long, userId: Long)
      extends BettingManagerActorMessage

  final case class UpdateParticipantsPerBet(updatedBet: Bet)
      extends BettingManagerActorMessage

  case object GetRunningBets extends BettingManagerActorMessage

  case object GetChildren extends BettingManagerActorMessage

  final case class ForwardProfit(profitVector: Vector[BetUserResult])

  def findRunningBetById(betId: Long,
                         bets: Vector[ManagingBet]): Option[ManagingBet] = {
    bets find { _._1 == betId }
  }

  def findRunningBetsByUserId(
      userId: Long,
      bets: Vector[ManagingBet],
      currentParticipantsPerBet: Vector[ParticipantsPerBet])
    : Vector[Option[ManagingBet]] = {
    val filterdTupels
      : Vector[ParticipantsPerBet] = currentParticipantsPerBet filter {
      case (betId, participants) => participants.contains(userId)
    }
    filterdTupels map { tupel =>
      findRunningBetById(tupel._1, bets)
    }
  }

}

final class BettingManagerActor(
    betDAO: BetDAO,
    cryptoPriceFetchActor: ActorRef @@ CryptoPriceFetchActorTag,
    userManagerActor: ActorRef @@ UserManagerActorTag)
    extends Actor
    with ActorLogging {

  import BettingActor._
  import akka.pattern.{ask, pipe}
  import com.softwaremill.macwire.akkasupport._

  implicit val timeout: Timeout = 5 seconds
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val mat: Materializer = ActorMaterializer()

  //ManagingBet = (betId, actorPathName)
  var runningBets: Vector[ManagingBet] = Vector.empty
  //ParticipantsPerBet = (String, Vector[Long])
  var currentParticipantsPerBet: Vector[ParticipantsPerBet] = Vector.empty

  def receive: Receive = {

    case CreateBet(createBetRequest: CreateBetRequest) =>
      val betActor: ActorRef = context.actorOf(wireProps[BettingActor])
      val betTimerActor: ActorRef =
        context.actorOf(wireProps[BettingTimerActor])
      val betFuture = for {
        maybeBetFromDB <- betDAO
          .createBet(
            Bet(
              None,
              startTime = 0L,
              stopTime = 0L,
              duration = 0L,
              bettingCategory = BettingCategory.Closed,
              bettingTopic = BettingTopic.NoTopic,
              startPriceInScalaCoins = 0D,
              endPriceInScalaCoins = 0D,
              prediction = 0D,
              poolInScalaCoins = 0D,
              participants = Vector.empty[Long],
              running = true
            ))
        maybeBet <- (betActor ? Create(createBetRequest,
                                       maybeBetFromDB.id.getOrElse(0)))
          .mapTo[Bet]
      } yield maybeBet

      betTimerActor ! StartTimer(betActor, createBetRequest.duration)
      betFuture
        .mapTo[Bet]
        .pipeTo(sender)

    case UpdateParticipantsPerBet(betUpdate: Bet) =>
      betUpdate.id match {
        case Some(betUpdateId) =>
          currentParticipantsPerBet = currentParticipantsPerBet filterNot {
            case (currentBetId, _) => betUpdateId == currentBetId
          }
          currentParticipantsPerBet = currentParticipantsPerBet :+ (betUpdateId, betUpdate.participants)
        case None => log.error("no id available")
      }

    case UpdateBettingManagerState(bet: Bet, betActorPathName: String) =>
      bet.id match {
        case Some(betIdUpdate) =>
          runningBets = runningBets filterNot {
            case (currentBetId, _) => betIdUpdate == currentBetId
          }
          runningBets = runningBets :+ (betIdUpdate, betActorPathName)
        case None => log.error("no id available")
      }

    case JoinUserToBet(joinBetRequest: JoinBetRequest) =>
      findRunningBetById(joinBetRequest.betId, runningBets) match {
        case Some(bet) =>
          val betActorPathName = bet._2
          context.child(betActorPathName) match {
            case Some(betActorChild) =>
              betActorChild.actorRef ! Join(joinBetRequest)
            case None =>
              log.error("no child for this bet actorRef", bet._2, " available")
          }
        case None =>
          log.error("no bet available for this id ", joinBetRequest.betId)
      }

    case GetBetJoinState(betId: Long, userId: Long) =>
      findRunningBetById(betId, runningBets) match {
        case Some(bet) =>
          val betActorPathName = bet._2
          context.child(betActorPathName) match {
            case Some(betActor) =>
              betActor.actorRef ? JoinState(userId) pipeTo sender
            case None => log.error("no such betActor")
          }
        case None =>
          log.error("no bet available for this id ", betId)
      }

    case ForwardProfit(profitVector: Vector[BetUserResult]) =>
      userManagerActor ! DistributeProfit(profitVector)

    case TerminateBet(actorRef: ActorRef) =>
      val betActorPathName = actorRef.path.name
      context.child(betActorPathName) match {
        case Some(betActor) =>
          betActor.actorRef ! Finish
          runningBets = runningBets filterNot {
            case (_, currentBetActorPathName) =>
              betActor.path.name == currentBetActorPathName
          }
        case None => log.error("no such actor")
      }

    case _ => log.info("not specified Message")
  }
}
