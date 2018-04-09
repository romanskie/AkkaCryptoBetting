package client.components

import client.Client

import scala.scalajs.js
import scala.xml.{Group, Node}

object Header {
  def render: Node = {
    val hidden = Client.rxSession.map(
      sessionId =>
        "dropdown-button btn blue-grey darken-4" + (if (sessionId) ""
                                                    else " hide"))
    val link = Client.rxUserId.map(userId => s"#/user/${userId.getOrElse(0L)}")

    <div class="navbar-fixed">
      <nav>
        <div class="nav-wrapper grey darken-4">
          <a class={ hidden } data-activates='dropdown1' data-hover="true">{ Client.rxChannel }</a>
          <a href={ link } class="brand-logo center">
            <img style="width: 30px; height: 30px;" src="assets/logo-mini.png"></img> $calaBetting
          </a>
          <ul class="right"> { Client.rxSession.map(renderRight) }</ul>
        </div>
      </nav>
      <ul id='dropdown1' class='dropdown-content' >
        { Client.rxChannels.map(_.map(renderChannel)) }
      </ul>
    </div>
  }

  def renderChannel(channel: String): Node = {
    <li><a href={ "#/channel/" + channel }>{ channel }</a></li>
  }

  def renderRight(session: Boolean): Node = {

    if (!session)
      <div></div>
    else
      Group(
        Seq(
          <li>Hello { Client.rxUsername }! Balance: { Client.rxBalance }$C</li>,
          // <li>Hello <a href={ link }>{ Client.rxUsername }</a>! Balance: { Client.rxBalance }$C</li>,
          <li><a class="btn red" onclick={ _: js.Dynamic => Client.logout() }>Logout</a></li>
        ))
  }
}
