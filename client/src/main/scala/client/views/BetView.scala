package client.views

import client.Client
import com.highcharts.HighchartsAliases._
import com.highcharts.HighchartsUtils._
import com.highcharts.config._
import models.betting.{Bet, BettingCategory, BettingTopic}
import org.scalajs.dom
import org.scalajs.jquery.jQuery

import scala.scalajs.js
import scala.scalajs.js.Date
import scala.scalajs.js.JSConverters._
import scala.xml.{Group, Node}

class ChartConfig(betId: Long) extends HighchartsConfig {
  override val chart: Cfg[Chart] = Chart(
    `type` = "pie",
    options3d = ChartOptions3d(alpha = 45, beta = 0, enabled = true))

  override val title: Cfg[Title] = Title(text = "Share")

  override val tooltip: Cfg[Tooltip] = Tooltip(
    pointFormat = "{series.name}: <b>{point.percentage:.1f}%</b>")

  override val plotOptions: Cfg[PlotOptions] = PlotOptions(
    pie = PlotOptionsPie(
      allowPointSelect = true,
      cursor = "pointer",
      depth = 35,
      dataLabels = PlotOptionsPieDataLabels(enabled = true,
                                            format = "{point.name}",
                                            color = "#ffffff"),
      size = "100%"
    ))
  override val series: SeriesCfg = js.Array[AnySeries](
    SeriesPie(
      name = "Bet",
      data = Client.statistics
        .filter(_._1._1 == betId)
        .map(result =>
          SeriesPieData(
            name = Client.users.getOrElse(result._2.userID, "Unknown").toString,
            y = result._2.gain))
        .toJSArray

//      data = js.Array[SeriesPieData](
//        SeriesPieData(name = "Test", y = 99.0),
//        SeriesPieData(name = "Test 2", y = 1.0)
//      )
    )
  )
}

object BetView {
  def render(betId: Long): Node = {
    val bet = for {
      bets <- Client.rxBets
    } yield {
      bets.filter(_.id.getOrElse(0L) == betId).head
    }

    jQuery(dom.document).ready { () =>
      jQuery("#bet-chart").highcharts(new ChartConfig(betId))
    }

    <div id="bet-view">
      <div class="row">
        <div id="bet-chart" class="center-align"></div>
      </div>
      <div class="row">
        <div class="card-panel grey darken-4 bet-view-bets">
          { bet.map { bet =>
          Group(Seq(
            <p>Topic: { topic(bet.bettingTopic) } </p>,
            <p>Category: { category(bet.bettingCategory) }</p>,
            <p>Bet: { if (bet.prediction >= 0D) "+" else "-" } { bet.prediction * 100 }%</p>,
            <p>Start: { new Date(bet.startTime).toLocaleString }</p>,
            <p>End: { new Date(bet.stopTime).toLocaleString }</p>,
            <p>Pool: { bet.poolInScalaCoins }$C</p>,
            <p>Participants:</p>,
            <ul>
              { renderParticipants(bet) }
            </ul>
          ))
        }}
        </div>
      </div>
    </div>

  }

  def topic(topic: BettingTopic): String = {
    topic match {
      case BettingTopic.Ethereum => "Ethereum"
      case _                     => "Bitcoin"
    }
  }

  def category(category: BettingCategory): String = {
    category match {
      case BettingCategory.Open => "Open"
      case _                    => "Closed"
    }
  }

  def renderParticipants(bet: Bet): Group = {
    Group(bet.participants.map(userId => <li class="bet-view-bet">
        <a href={ s"#/user/$userId" }>{ Client.users.getOrElse(userId, "Unknown user") }</a>
        {
          Client.statistics.get((bet.id.getOrElse(0L), userId)).map(result =>
            <span>won { result.gain } $C</span>
          )
        }
      </li>))
  }
}
