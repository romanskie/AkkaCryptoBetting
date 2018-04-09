package client

import com.definitelyscala.materializecss.Materializecss.Materialize
import io.circe.parser.decode
import io.circe.syntax._
import mhtml.{Rx, Var, mount}
import models.betting.request.{CreateBetRequest, JoinBetRequest}
import models.betting.{Bet, BettingCategory, BettingTopic}
import models.user.User
import models.user.request.BetUserResult
import org.scalajs.dom.document
import org.scalajs.dom.ext.Ajax

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js.Date
import scala.scalajs.js.timers._
import scala.util.{Failure, Success}

object Client {
  val host = "http://localhost:9000"
  var channels: Seq[String] = Seq()
  var users: Map[Long, String] = getUsers
  val bets: Map[Long, Bet] = Map()
  var statistics: Map[(Long, Long), BetUserResult] = Map()
  var channel: String = "Channel"
  var balance: Double = 0
  var userId: Long = 0
  lazy val rxBets: Var[Seq[Bet]] = Var(Seq())
  lazy val rxBalance: Var[Double] = Var(balance)
  lazy val rxUserId: Var[Option[Long]] = Var(Some(userId))
  val rxChannels: Rx[Seq[String]] = getChannels
  val rxRunningBets: Rx[Seq[Bet]] = updateRunningBets(1000)
  val rxFinishedBets: Rx[Seq[Bet]] = updatedFinishedBets(1000)
  lazy val rxSession: Var[Boolean] = Var(false)
  lazy val rxUsername: Var[String] = Var("")
  var rxChannel: Var[String] = Var(channel)

  def main(args: Array[String]): Unit = {
    updateBets()
    updateStats()

    setInterval(2500) {
      updateBets()
      updateStats()
    }

    mount(document.body, Router.route)
  }

  def setChannel(channel: String): Unit = {
    this.channel = channel
    rxChannel := channel
  }

  def setBalance(balance: Double): Unit = {
    this.balance = balance
    rxBalance := balance
  }

  def setUserId(userId: Option[Long]): Unit = {
    this.userId = userId.getOrElse(0)
    rxUserId := userId
  }

  def getChannels: Rx[Seq[String]] = {
    val rxChannels = Var[Seq[String]](Seq())

    for {
      response <- Ajax.get(s"$host/betting/topics",
                           headers = Map("Content-Type" -> "application/json"))
    } yield {
      decode[Seq[String]](response.responseText).foreach { seq =>
        rxChannels := seq
        channels = seq
      }
    }

    rxChannels
  }

  def updateBets(): Unit = {
    for {
      response <- Ajax.get(s"$host/betting/bets",
                           headers = Map("Content-Type" -> "application/json"))
    } yield {
      decode[Seq[Bet]](response.responseText).foreach {
        rxBets := _
      }
    }
  }

  def updateRunningBets(interval: Long): Rx[Seq[Bet]] = {
    val lastRefresh: Var[Double] = Var(Date.now)

    setInterval(interval) {
      lastRefresh := Date.now
    }

    for {
      bets <- rxBets
      _ <- lastRefresh
    } yield {
      bets.filter(_.stopTime > Date.now)
    }
  }

  def updatedFinishedBets(interval: Long): Rx[Seq[Bet]] = {
    val lastRefresh: Var[Double] = Var(Date.now)

    setInterval(interval) {
      lastRefresh := Date.now
    }

    for {
      bets <- rxBets
      _ <- lastRefresh
    } yield {
      bets.filter(_.stopTime <= Date.now)
    }
  }

  def updateStats(): Unit = {
    for {
      response <- Ajax.get(s"$host/stats/all",
                           headers = Map("Content-Type" -> "application/json"))
    } yield {
      decode[Seq[BetUserResult]](response.responseText).foreach { seq =>
        val balanceDiff = seq
          .filter(
            result =>
              result.userID == userId && !statistics.contains(
                (result.betID, result.userID)))
          .map { result =>
            Materialize.toast(s"You just won ${result.gain} $$C!",
                              displayLength = 5000)
            result.gain
          }
          .sum

        setBalance(balance + balanceDiff)
        statistics =
          seq.map(result => (result.betID, result.userID) -> result).toMap
      }
    }
  }

  def getUsers: Map[Long, String] = {
    for {
      response <- Ajax.get(s"$host/user/users",
                           headers = Map("Content-Type" -> "application/json"))
    } yield {
      decode[Seq[User]](response.responseText).foreach { seq =>
        users = seq
          .map(user =>
            user.id.getOrElse(0L) -> s"${user.firstName} ${user.lastName}")
          .toMap
      }
    }
    Map()
  }

  def logout(): Unit = {
    rxSession := false
    Ajax.post(s"${Client.host}/user/logout",
              data = "",
              headers = Map("Content-Type" -> "application/json"))
  }

  def createBet(userId: Long,
                channel: String,
                currentBalance: Double,
                duration: Long,
                investment: Double,
                prediction: Double,
                isOpenBet: Boolean): Unit = {
    val topic =
      if (channel == "Bitcoin") BettingTopic.Bitcoin else BettingTopic.Ethereum
    val category =
      if (isOpenBet) BettingCategory.Open else BettingCategory.Closed

    val request = CreateBetRequest(
      userId,
      duration * 60000,
      investment,
      prediction,
      bettingCategory = category,
      bettingTopic = topic
    ).asJson.toString

    Ajax
      .post(s"${host}/betting/bet",
            data = request,
            headers = Map("Content-Type" -> "application/json"))
      .onComplete {
        case Success(_) =>
          Materialize.toast("Your bet has been placed", displayLength = 5000)
          Client.setBalance(Client.balance - investment)
          updateBets()
        case Failure(error) =>
          Materialize.toast(error.getMessage, displayLength = 5000)
      }
  }

  def joinBet(userId: Long,
              betId: Long,
              investment: Double,
              prediction: Double,
              balance: Double): Unit = {
    val request =
      JoinBetRequest(betId, userId, investment, prediction).asJson.toString

    println(request)

    Ajax("PATCH",
         s"${Client.host}/betting/bet/join",
         request,
         timeout = 0,
         headers = Map("Content-Type" -> "application/json"),
         withCredentials = false,
         responseType = "").onComplete {
      case Success(response) =>
        Materialize.toast("Successfully joined bet", displayLength = 5000)
        Client.setBalance(Client.balance - investment)
        updateBets()
      case Failure(error) =>
        Materialize.toast(error.getMessage, displayLength = 5000)
    }
  }
}
