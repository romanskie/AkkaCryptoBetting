package actors.betting

import actors.betting.BettingManagerActor.TerminateBet
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Timers}
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

sealed trait BettingTimerActorTag
sealed trait BettingTimerActorMessage

object BettingTimerActor {

  def props: Props =
    Props(new BettingTimerActor())

  final case class StartTimer(bettingActor: ActorRef, duration: Long)
      extends BettingTimerActorMessage

  case object FetchPrice extends BettingTimerActorMessage

  final case class StopTimer(bettingActor: ActorRef)
      extends BettingTimerActorMessage
}

final class BettingTimerActor extends Actor with Timers with ActorLogging {
  import BettingTimerActor._

  implicit val timeout: Timeout = 5 seconds
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val mat: Materializer = ActorMaterializer()

  def receive = {

    case StartTimer(bettingActor: ActorRef, duration: Long) =>
      context.system.scheduler
        .scheduleOnce(duration millis, self, StopTimer(bettingActor))

      log.info(
        s"here we start The timer with current Time for BettingActor $bettingActor : with duration of : $duration (ms)")

    case StopTimer(bettingActor: ActorRef) =>
      log.info(s"Timer killed at for betting actor with id: $bettingActor")
      context.parent ! TerminateBet(bettingActor)
      context.stop(self)
    case _ =>
  }

}
