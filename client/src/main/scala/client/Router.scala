package client

import client.components._
import client.views._
import mhtml.{Rx, Var}
import org.scalajs.dom
import org.scalajs.dom.Event

import scala.util.matching.Regex
import scala.xml.{Group, Node}

object Router {
  val rxPath: Rx[String] = Rx(dom.window.location.hash).merge {
    val hash = Var(dom.window.location.hash)
    dom.window.onhashchange = (_: Event) => {
      println(dom.window.location.hash)
      if (dom.window.location.hash != "#")
        hash := dom.window.location.hash
    }
    hash.map(url => url.replaceFirst("#/", ""))
  }

  val index: Regex = """""".r
  val channel: Regex = """channel/([^/]+)/?$""".r
  val user: Regex = """user/([0-9]+)/?$""".r
  val login: Regex = """login/?$""".r
  val bet: Regex = """bet/([0-9]+)/?$""".r

  def route: Node = {
    val node = for {
      session <- Client.rxSession
      path <- rxPath
    } yield {
      if (session) currentView(path) else LoginView.render
    }

    Group(Seq(Header.render, <div>{ node }</div>))
  }

  def redirect(segment: String): Unit = {
    dom.window.location.assign(segment)
  }

  def currentView(path: String): Node = {
    path match {
      case index() =>
        Client.setChannel(Client.channels.head)
        ChannelView.render(Client.channels.head)
      case channel(ch) =>
        Client.setChannel(ch)
        ChannelView.render(ch)
      case default =>
        Client.setChannel("Channel")
        default match {
          case login() =>
            LoginView.render
          case user(id) =>
            UserView.render(id.toLong)
          case bet(id) =>
            BetView.render(id.toLong)
          case default =>
            println(s"No route found for $default")
            <h1 style="font-size: 100pt;" class="center-align">404: Not Found</h1>
        }
    }
  }
}
