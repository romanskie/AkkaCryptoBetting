package modules

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.util.Timeout
import controllers.{
  ApplicationController,
  BettingController,
  SocketController,
  UserController
}
import dao.{BetDAO, StatsDAO, UserDAO}
import play.api.mvc.ControllerComponents

import scala.concurrent.ExecutionContext

trait ControllerModule extends ActorModule {

  import com.softwaremill.macwire._

  implicit def ec: ExecutionContext
  implicit def actorSystem: ActorSystem
  implicit def materializer: Materializer
  implicit def timeout: Timeout

  def statsDao: StatsDAO
  def betDao: BetDAO
  def userDao: UserDAO
  def controllerComponents: ControllerComponents

  lazy val socketController: SocketController = wire[SocketController]
  lazy val userController: UserController = wire[UserController]
  lazy val bettingController: BettingController = wire[BettingController]
  lazy val applicationController: ApplicationController =
    wire[ApplicationController]

}
