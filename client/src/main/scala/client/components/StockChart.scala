package client.components

import com.highcharts.HighchartsUtils._
import com.highstock.HighstockAliases._
import com.highstock.config._
import io.circe.parser.decode
import models.crypto.CryptoPrice
import org.scalajs.dom
import org.scalajs.dom.MessageEvent
import org.scalajs.jquery.jQuery

import scala.scalajs.js
import scala.xml.Node

class StockChartConfig(topic: String,
                       stockData: Map[String, js.Array[js.Array[js.Any]]])
    extends HighstockConfig {
//  override val title: Cfg[Title] = Title(text = "Stock chart")
//  override val subtitle: Cfg[Subtitle] = Subtitle(text = "Using ordinal X axis")
  override val xAxis: CfgArray[XAxis] = js.Array(XAxis(gridLineWidth = 0))
  override val rangeSelector: Cfg[RangeSelector] = RangeSelector(
    buttons = js.Array(
      RangeSelectorButtons(`type` = "minute", count = 1, text = "1M"),
      RangeSelectorButtons(`type` = "hour", count = 1, text = "1H"),
      RangeSelectorButtons(`type` = "day", count = 1, text = "1D"),
      RangeSelectorButtons(`type` = "all", count = 1, text = "All")
    ),
    selected = 3,
    inputEnabled = false
  )

  override val series: SeriesCfg = js.Array[AnySeries](
    SeriesArea(
      name = "Ethereum",
      visible = topic == "Ethereum",
      gapSize = 5,
      tooltip = SeriesAreaTooltip(valueDecimals = 2),
      threshold = null,
      data = stockData("ether")
    ),
    SeriesArea(
      name = "Bitcoin",
      visible = topic == "Bitcoin",
      gapSize = 5,
      tooltip = SeriesAreaTooltip(valueDecimals = 2),
      threshold = null,
      data = stockData("bitcoin")
    )
  )
}

object StockChart {
  val socket: dom.WebSocket = {
    val socket =
      new dom.WebSocket("ws://localhost:9000/cryptoPriceSubscription")
    socket.onmessage = onMessageReceived
    socket
  }

  val stockData: Map[String, js.Array[js.Array[js.Any]]] = Map(
    "ether" -> js.Array(js.Array[js.Any]()),
    "bitcoin" -> js.Array(js.Array[js.Any]()),
  )

  def render(channel: String): Node = {
    jQuery(dom.document).ready { () =>
      jQuery("#chart").highstock(new StockChartConfig(channel, stockData))
    }

    <div class="row">
      <div id="chart" class="card-panel grey darken-4"></div>
    </div>
  }

  def onMessageReceived(e: MessageEvent): Unit = {
    decode[CryptoPrice](e.data.toString).foreach(price => {
      val x: Double = js.Date.now()
      val y: Double = price.priceInEuro
      stockData(price.name).append(js.Array[js.Any](x, y))
      jQuery("#chart")
        .highcharts()
        .foreach(chart => {
          val series =
            if (price.name == "ether") chart.series(0) else chart.series(1)
          series.addPoint(js.Array[js.Any](x, y), redraw = true)
        })
    })
  }
}
