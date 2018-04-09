package modules

import actors.betting.{BettingManagerActor, BettingManagerActorTag}
import actors.crypto.{CryptoPriceFetchActor, CryptoPriceFetchActorTag}
import actors.user.{UserManagerActor, UserManagerActorTag}
import akka.actor.{ActorRef, ActorSystem}
import akka.stream.Materializer
import akka.util.Timeout
import com.softwaremill.macwire.akkasupport.wireProps
import dao.{BetDAO, StatsDAO, UserDAO}

import scala.concurrent.ExecutionContext

trait ActorModule {

  import com.softwaremill.tagging._

  implicit def ec: ExecutionContext
  implicit def actorSystem: ActorSystem
  implicit def materializer: Materializer
  implicit def timeout: Timeout

  def betDao: BetDAO
  def userDao: UserDAO
  def statsDao: StatsDAO

  lazy val cryptoPriceFetchActor: ActorRef @@ CryptoPriceFetchActorTag =
    actorSystem
      .actorOf(wireProps[CryptoPriceFetchActor])
      .taggedWith[CryptoPriceFetchActorTag]

  lazy val bettingManagerActor: ActorRef @@ BettingManagerActorTag =
    actorSystem
      .actorOf(wireProps[BettingManagerActor])
      .taggedWith[BettingManagerActorTag]

  lazy val userManagerActor: ActorRef @@ UserManagerActorTag =
    actorSystem
      .actorOf(wireProps[UserManagerActor])
      .taggedWith[UserManagerActorTag]

}
