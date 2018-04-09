package controllers

import actors.betting.BettingManagerActor._
import actors.betting.BettingManagerActorTag
import actors.user.UserManagerActor.UserJoinable
import actors.user.UserManagerActorTag
import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.stream.Materializer
import akka.util.Timeout
import com.softwaremill.tagging.@@
import dao.BetDAO
import io.circe.syntax._
import models.betting._
import models.betting.request.{CreateBetRequest, JoinBetRequest}
import play.api.libs.circe.Circe
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

final class BettingController(
    userManagerActor: ActorRef @@ UserManagerActorTag,
    bettingManagerActor: ActorRef @@ BettingManagerActorTag,
    cc: ControllerComponents,
    betDAO: BetDAO)(implicit ec: ExecutionContext,
                    actorSystem: ActorSystem,
                    materializer: Materializer,
                    timeout: Timeout)
    extends AbstractController(cc)
    with Circe {

  def validateCreateBetRequest(createBetRequest: CreateBetRequest) = {
    if (createBetRequest.duration < 60000 | createBetRequest.investInScalaCoins <= 0.0)
      false
    else true
  }

  def createBet = Action.async(circe.json[CreateBetRequest]) {
    implicit request =>
      val req = request.body

      validateCreateBetRequest(req) match {
        case false =>
          Future(
            InternalServerError(
              "Cant create bet. Duration or invest is wrong.".asJson))

        case true =>
          val f1 = (userManagerActor ? UserJoinable(req.userId,
                                                    req.investInScalaCoins))
            .mapTo[Boolean]
          val f2 = (bettingManagerActor ? CreateBet(req)).mapTo[Bet]
          for {
            v1 <- f1
            v2 <- f2
          } yield {
            v1 match {
              case true => Ok(v2.asJson)
              case false =>
                InternalServerError(
                  "You cant join that bet anymore. Jointime (5 min) is over")
            }
          }
      }
  }

  def validateJoinBetRequest(joinBetRequest: JoinBetRequest) = {
    if (joinBetRequest.investInScalaCoins <= 0.0) false
    else true
  }

  def joinBet = Action.async(circe.json[JoinBetRequest]) { implicit request =>
    val req = request.body

    validateJoinBetRequest(req) match {
      case false =>
        Future(InternalServerError("Your invest cant be 0 or negative".asJson))
      case true =>
        val f1 =
          (bettingManagerActor ? GetBetJoinState(req.betId, req.userId))
            .mapTo[Boolean]
        val f2 =
          (userManagerActor ? UserJoinable(req.userId, req.investInScalaCoins))
            .mapTo[Boolean]
        for {
          v1 <- f1
          v2 <- f2
        } yield {
          v1 match {
            case true =>
              v2 match {
                case true =>
                  bettingManagerActor ! JoinUserToBet(req)
                  Ok("You joined to bet " + req.betId.asJson)
                case false =>
                  InternalServerError(
                    "Your wallet is to slim to join this bet!")
              }
            case false =>
              InternalServerError("You cant join that bet anymore.")
          }
        }
    }
  }

  def getBetById(betId: Long) = Action.async { implicit request =>
    betDAO.getBetById(betId) map {
      case Some(bet) => Ok(bet.asJson)
      case None      => InternalServerError("No such bet for id " + betId.asJson)
    }
  }

  def getBets = Action.async { implicit request =>
    betDAO.getBets map {
      case Seq() => InternalServerError("Currently no running bets".asJson)
      case bets  => Ok(bets.asJson)
    }
  }

  def getRunningBets = Action.async { implicit request =>
    betDAO.getRunningBets map {
      case Seq() => InternalServerError("Currently no running bets".asJson)
      case bets  => Ok(bets.asJson)
    }
  }

  def getClosedBets = Action.async { implicit request =>
    betDAO.getClosedBets map {
      case Seq() => InternalServerError("Currently no running bets".asJson)
      case bets  => Ok(bets.asJson)
    }
  }

  def getAllBets = Action.async { implicit request =>
    betDAO.getBets map {
      case Seq() => InternalServerError("Currently no bets available".asJson)
      case bets  => Ok(bets.asJson)
    }
  }

  def getBettingTopics = Action { implicit request =>
    val bettingTopics =
      Vector(BettingTopic.Bitcoin.toString, BettingTopic.Ethereum.toString)
    Ok(bettingTopics.asJson)
  }

  def getBettingCategories = Action { implicit request =>
    val bettingCategories =
      Vector(BettingCategory.Open.toString, BettingCategory.Closed.toString)
    Ok(bettingCategories.asJson)
  }

}
