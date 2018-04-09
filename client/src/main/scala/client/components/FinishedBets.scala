package client.components

import client.Client
import models.betting.Bet

import scala.scalajs.js
import scala.xml.Node

object FinishedBets {
  def render: Node = {
    val finishedBets = for {
      bets <- Client.rxFinishedBets
      channel <- Client.rxChannel
    } yield {
      bets
        .filter(_.bettingTopic.toString.toLowerCase == channel.toLowerCase)
        .sortBy(_.stopTime)
    }

    <div class="col s5">
      <div class="card-panel grey darken-4">
        <h5>Finished bets</h5>
        <table>
          <tr>
            <th>Bet</th>
            <th>Started</th>
            <th>Ended</th>
            <th>Pool</th>
          </tr>
          { finishedBets.map(_.map(renderBet)) }
        </table>
      </div>
    </div>
  }

  def renderBet(bet: Bet): Node = {
    <tr>
      <td>
        { if (bet.prediction >= 0D) "+" else "-" }
        { bet.prediction * 100 }%
      </td>
      <td>{ new js.Date(bet.startTime).toLocaleString() }</td>
      <td>{ new js.Date(bet.stopTime).toLocaleString() }</td>
      <td><a href={ s"#/bet/${bet.id.getOrElse(0L)}" }>{ bet.poolInScalaCoins } $C</a></td>
    </tr>
  }
}
