package client.views

import client.Client
import io.circe.parser.decode
import io.circe.syntax._
import mhtml._
import models.user.User
import models.user.request.LoginUserRequest
import org.scalajs.dom.Event
import org.scalajs.dom.ext.Ajax
import org.scalajs.jquery.jQuery

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}
import scala.xml.Node

object LoginView {
  val rxLoginFailed: Var[Boolean] = Var(false)

  def render: Node = {
    val hidden = rxLoginFailed.map(if (_) "" else "hide")

    <div class="valign-wrapper row login-box">
      <div class="col card hoverable s10 pull-s1 m6 pull-m3 l4 pull-l4">
        <form>
          <div class="card-content">
            <span class="card-title">Enter credentials</span>
            <div class="row">
              <div class="input-field col s12">
                <label for="email">Email address</label>
                <input type="email" class="validate" name="email" id="email" />
              </div>
              <div class="input-field col s12">
                <label for="password">Password </label>
                <input type="password" class="validate" name="password" id="password" />
              </div>
              <p class={ hidden } style="color: red;">Email and password did not match any account</p>
            </div>
          </div>
          <div class="card-action right-align">
            <input type="reset" id="reset" class="btn-flat grey-text waves-effect" />
            <a class="btn green waves-effect waves-light" value="Login"
               onclick={_: Event => login(email, password) }>Login</a>
          </div>
        </form>
      </div>
    </div>
  }

  def email: String = {
    jQuery("input[name=email]").value.toString
  }

  def password: String = {
    jQuery("input[name=password]").value.toString
  }

  def login(email: String, password: String): Unit = {
    val request = LoginUserRequest(email, password)
    val data = request.asJson.toString

    Ajax
      .post(s"${Client.host}/user/login",
            data,
            headers = Map("Content-Type" -> "application/json"))
      .onComplete {
        case Failure(error) =>
          rxLoginFailed := true
        case Success(response) =>
          decode[User](response.responseText).foreach { user =>
            Client.rxSession := true
            rxLoginFailed := false
            Client.rxUsername := s"${user.firstName} ${user.lastName}"
            // Client.rxBalance := user.wallet
            // Client.rxUserId := user.id
            Client.setUserId(user.id)
            Client.setBalance(user.wallet)
          }
      }
  }
}
