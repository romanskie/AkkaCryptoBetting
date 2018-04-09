package client.views

import client.components._

import scala.xml.Node

object ChannelView {
  def render(channel: String): Node = {
    <div class="row" id="channel-view">
      <div class="col s9">
        { StockChart.render(channel) }
        <div class="row">
          { RunningBets.render }
          { CreateBet.render }
          { FinishedBets.render }
        </div>
      </div>
      <div class="col s3">
        { Chat.render }
      </div>
    </div>
  }
}
