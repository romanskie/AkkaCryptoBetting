package actors.betting
import actors.betting.BettingManagerActor.{
  ForwardProfit,
  ManagingBet,
  UpdateBettingManagerState,
  UpdateParticipantsPerBet
}
import actors.crypto.CryptoPriceFetchActor.GetPrice
import actors.crypto.CryptoPriceFetchActorTag
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import com.softwaremill.tagging.@@
import dao.BetDAO
import models.betting._
import models.betting.request.{CreateBetRequest, JoinBetRequest}
import models.crypto.CryptoPrice
import models.user.request.{BetUserResult, ParticipantData}
import utils.BettingActorHelper._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

sealed trait BettingActorMessage

object BettingActor {

  def props(
      betDAO: BetDAO,
      cryptoPriceFetchActor: ActorRef @@ CryptoPriceFetchActorTag): Props =
    Props(new BettingActor(betDAO, cryptoPriceFetchActor))

  case object State extends BettingActorMessage

  case object Finish extends BettingActorMessage

  final case class Create(createBetRequest: CreateBetRequest, betId: Long)
      extends BettingActorMessage

  final case class JoinState(userId: Long) extends BettingActorMessage

  final case class Update(bet: Bet) extends BettingActorMessage //kein user update wenn wettdauer < 10 min#

  final case class Join(betRequest: JoinBetRequest) extends BettingActorMessage

  final case class UpdateEndPrice(cryptoPrice: CryptoPrice)
      extends BettingActorMessage

  final case class UpdatePrice(cryptoPrice: CryptoPrice)
      extends BettingActorMessage

  case object SwitchJoinState extends BettingActorMessage

  final case class UpdateParticipants(participantData: ParticipantData)
      extends BettingActorMessage

  final case class SetPrice(bet: Bet) extends BettingActorMessage

  final case class Test(managingBet: ManagingBet)

}

final class BettingActor(
    betDAO: BetDAO,
    cryptoPriceFetchActor: ActorRef @@ CryptoPriceFetchActorTag)
    extends Actor
    with ActorLogging {

  import java.time

  import BettingActor._
  import akka.pattern.{ask, pipe}

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val mat: Materializer = ActorMaterializer()
  implicit val timeout: Timeout = 5 second

  var currentBet: Bet = _
  var joinable: Boolean = true
  var currentPrice: CryptoPrice = _
  var participants: Vector[ParticipantData] = Vector.empty

  def findUserByIdInParticipants(
      userId: Long,
      participants: Vector[ParticipantData]): Boolean = {
    participants find { _.id == userId } match {
      case Some(participant) => true
      case None              => false
    }
  }

  def calculateGain(bet: Bet,
                    participantData: Vector[ParticipantData],
                    cryptoPrice: CryptoPrice): Vector[BetUserResult] = {

    val endGain = (cryptoPrice.priceInScalaCoins - bet.startPriceInScalaCoins) / bet.startPriceInScalaCoins

    val betId = bet.id match {
      case Some(id) => id
      case None     => -1
    }
    val plusGain = participantData filter (_.prediction > 0D)
    val minusGain = participantData filter (_.prediction < 0D)

    bet.bettingCategory match {
      case BettingCategory.Open =>
        calculateOpenBetResult(betId, bet, endGain, plusGain, minusGain)

      case BettingCategory.Closed =>
        calculateClosedBetResult(betId, bet, endGain, plusGain, minusGain)
    }
  }

  override def postStop: Unit = {
    val closedBet = currentBet.copy(running = false)
    betDAO.updateBet(closedBet)

  }

  override def preStart(): Unit = {
    context.system.scheduler.scheduleOnce(65 seconds, self, SwitchJoinState)
  }

  override def receive: Receive = {
    case Finish =>
      for {
        v1 <- (cryptoPriceFetchActor ? GetPrice(currentBet.bettingTopic))
          .mapTo[CryptoPrice]
      } yield {

        self ! UpdateEndPrice(v1)
        val profits = calculateGain(currentBet, participants, v1)
        context.parent ! ForwardProfit(profits)
        context.stop(self)

      }

    case UpdateEndPrice(cryptoPrice: CryptoPrice) =>
      val price = cryptoPrice.priceInScalaCoins
      val updatedBet = currentBet.copy(endPriceInScalaCoins = price)
      self ! Update(updatedBet)

    case UpdatePrice(cryptoPrice: CryptoPrice) =>
      val price = cryptoPrice.priceInScalaCoins
      val updatedBet = currentBet.copy(startPriceInScalaCoins = price)
      self ! Update(updatedBet)
      self forward State

    case Join(joinBetRequest: JoinBetRequest) =>
      if (!joinable)
        log.debug("bet ist not joinable anymore!")
      else {
        val updatedBet = currentBet.copy(
          poolInScalaCoins = currentBet.poolInScalaCoins + joinBetRequest.investInScalaCoins,
          participants = currentBet.participants :+ joinBetRequest.userId
        )

        val updatedParticipantData = ParticipantData(
          id = joinBetRequest.userId,
          invest = joinBetRequest.investInScalaCoins,
          prediction = joinBetRequest.prediction,
          joinTime = time.Clock.systemDefaultZone().millis()
        )
        self ! Update(updatedBet)
        self ! UpdateParticipants(updatedParticipantData)

      }

    case Create(createBetRequest: CreateBetRequest, betId: Long) =>
      val createdBet = Bet(
        id = Some(betId),
        startTime = time.Clock.systemDefaultZone().millis(),
        stopTime = time.Clock
          .systemDefaultZone()
          .millis() + createBetRequest.duration,
        duration = createBetRequest.duration,
        bettingTopic = createBetRequest.bettingTopic,
        bettingCategory = createBetRequest.bettingCategory,
        startPriceInScalaCoins = 0D, //TODO: KEINE OPTIONS MEHR
        endPriceInScalaCoins = 0D,
        prediction = createBetRequest.prediction,
        poolInScalaCoins = createBetRequest.investInScalaCoins,
        participants = Vector(createBetRequest.userId),
        running = true
      )

      val participantData = ParticipantData(
        id = createBetRequest.userId,
        invest = createBetRequest.investInScalaCoins,
        prediction = createBetRequest.prediction,
        joinTime = time.Clock.systemDefaultZone().millis()
      )

      context.parent ! UpdateBettingManagerState(createdBet, self.path.name)
      self ! Update(createdBet)
      self ! UpdateParticipants(participantData)

      (cryptoPriceFetchActor ask GetPrice(createBetRequest.bettingTopic))
        .mapTo[CryptoPrice]
        .map(UpdatePrice)
        .pipeTo(self)(sender)

    case Update(bet: Bet) =>
      currentBet = bet
      betDAO.updateBet(currentBet)

    case UpdateParticipants(participantData: ParticipantData) =>
      participants = participants :+ participantData
      context.parent ! UpdateParticipantsPerBet(currentBet)

    case State =>
      sender ! currentBet

    case JoinState(userId: Long) =>
      val exists = findUserByIdInParticipants(userId, participants)
      if (!joinable) sender ! false
      if (exists) sender ! false
      else sender ! true

    case SwitchJoinState =>
      currentBet.bettingCategory match {
        case BettingCategory.Closed => joinable = false
        case BettingCategory.Open   => joinable = true
      }

    case _ => log.error("Message not accepted")

  }

}

sealed trait BettingActorTag
