package client.views

import client.Client
import models.betting.Bet

import scala.xml.Node

object UserView {
  def render(userId: Long): Node = {
    val userRunningBets =
      Client.rxRunningBets.map(_.filter(_.participants.contains(userId)))
    val userFinishedBets =
      Client.rxFinishedBets.map(_.filter(_.participants.contains(userId)))
    val username = Client.users.getOrElse(userId, "Unknown user")

    <div class="row">
      <div class="card-panel grey darken-4" id="user-view">
        <h4 class="center-align">{ username }</h4>
        <div id="user-view-active-bets">
          <h5>Active bets</h5>
          <ul>{ userRunningBets.map(_.map(renderBet)) }</ul>
        </div>
        <div id="user-view-finished-bets">
          <h5>Finished bets</h5>
          <ul>{ userFinishedBets.map(_.map(renderBet)) }</ul>
        </div>
      </div>
    </div>
  }

  def renderBet(bet: Bet): Node = {
    val betId = bet.id.getOrElse(0L)

    <li class="user-view-bet">
      <a href={ s"#/bet/$betId" }>{ bet.bettingTopic.toString }</a>
    </li>
  }
}
