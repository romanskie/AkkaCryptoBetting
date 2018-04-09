package client.components

import client.Client
import models.betting.{Bet, BettingCategory}
import com.definitelyscala.materializecss.Materializecss.Materialize
import org.scalajs.jquery.jQuery
import scala.scalajs.js
import scala.scalajs.js.Date
import scala.xml.{Group, Node}

object RunningBets {
  def render: Node = {
    val runningBets = for {
      bets <- Client.rxRunningBets
      channel <- Client.rxChannel
    } yield {
      bets
        .filter(_.bettingTopic.toString.toLowerCase == channel.toLowerCase)
        .sortBy(_.stopTime)
    }

    <div class="col s5">
      <div class="card-panel grey darken-4">
        <div class="row">
          <div class="col s6">
            <h5>Running bets</h5>
          </div>
          <!--
          <div class="col s6">
            <div class="row right">
              <input type="number" name="join-invest" min="1" max={ Client.rxBalance.map(_.toString) } value="1"/>
            </div>
          </div>
          -->
        </div>
        <table id="running-bets-table">
          <tr>
            <th>Bet</th>
            <th>Time left</th>
            <th>Pool</th>
            <th></th>
          </tr>
          { runningBets.map(_.map(renderBet)) }
        </table>
      </div>
    </div>
  }

  def renderBet(bet: Bet): Node = {
    val button = for {
      optUserId <- Client.rxUserId
      balance <- Client.rxBalance
    } yield {
      val userId = optUserId.getOrElse(0L)
      val betId = bet.id.getOrElse(0L)

      def onClick(): Unit = {
        val investment = jQuery("input[name=invest]").value
          .asInstanceOf[String]
          .toDouble

        val prediction = jQuery("input[name=prediction]").value
          .asInstanceOf[String]
          .toDouble / 100

        if (investment <= 0D) {
          Materialize.toast("Can't join bet with investment <= 0",
                            displayLength = 5000)
        } else if (investment > Client.balance) {
          Materialize.toast("You don't have enough $C", displayLength = 5000)
        } else {
          Client.joinBet(userId, betId, investment, prediction, balance)
        }
      }

      if (bet.participants.contains(userId) || bet.bettingCategory == BettingCategory.Closed && Date.now > bet.startTime + 60000 * 5)
        <div></div>
      else
        <a class='btn blue-grey darken-4' onclick={ () => onClick() }>Join</a>
    }

    <tr class="running-bet-row">
      <td>
        { if (bet.prediction >= 0D) "+" else "-" }
        { bet.prediction * 100 }%
      </td>
      <td>{ ((bet.stopTime - js.Date.now) / 1000).toLong  }s</td>
      <td><a href={ s"#/bet/${bet.id.getOrElse(0L)}" }>{ bet.poolInScalaCoins } $C</a></td>
      <td>{ button }</td>
    </tr>
  }
}
