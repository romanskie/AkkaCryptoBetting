package client.components

import client.Client
import com.definitelyscala.materializecss.Materializecss.Materialize
import org.scalajs.dom.document
import org.scalajs.dom.html.Input
import org.scalajs.jquery.jQuery

import scala.scalajs.js
import scala.xml.Node

object CreateBet {
  val defaultDuration: Long = 5
  val defaultInvest: Long = 10

  def render: Node = {
    <div class="col s2">
      <div class="card-panel grey darken-4">
        <div class="row">
          <p>
            <input type="number" name="duration" min="1" step="1" value={ defaultDuration.toString }/>
            <label for="duration">Duration in minutes</label>
          </p>
          <p>
            <input type="number" name="invest" min="1" max={ Client.rxBalance.map(_.toString) } value={ defaultInvest.toString }/>
            <label for="invest">Invest ($C)</label>
          </p>
          <p>
            <input type="number" name="prediction" value="5"/>
            <label for="prediction">Prediction in %</label>
          </p>
          <p>
            <input type="checkbox" id="category" name="category" />
            <label for="category">Allow join after 5 minutes</label>
          </p>
          <div class="row center-align">
            <a class="btn blue-grey darken-4" onclick={ onClick _ }>Create bet</a>
          </div>
        </div>
      </div>
    </div>
  }

  def onClick(e: js.Dynamic): Unit = {
    val duration = jQuery("input[name=duration]").value
      .asInstanceOf[String]
      .toLong
    val investment = jQuery("input[name=invest]").value
      .asInstanceOf[String]
      .toDouble
    val prediction = jQuery("input[name=prediction]").value
      .asInstanceOf[String]
      .toDouble / 100
    val isOpenBet =
      document.getElementById("category").asInstanceOf[Input].checked
    val balance = Client.balance
    if (verify(duration, investment, prediction, balance))
      Client.createBet(Client.userId,
                       Client.channel,
                       balance,
                       duration,
                       investment,
                       prediction,
                       isOpenBet)
  }

  def verify(duration: Long,
             investment: Double,
             prediction: Double,
             balance: Double): Boolean = {
    if (duration < 1) {
      Materialize.toast("Can't create bet with a duration of < 1",
                        displayLength = 5000)
      false
    } else if (investment > balance) {
      Materialize.toast("You don't have enough $C", displayLength = 5000)
      false
    } else if (investment <= 0) {
      Materialize.toast("Can't create bet with 0 investment",
                        displayLength = 5000)
      false
    } else if (prediction == 0D) {
      Materialize.toast("Can't create bet with 0 prediction",
                        displayLength = 5000)
      false
    } else {
      true
    }
  }
}
