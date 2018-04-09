package client.components

import client.Client
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._
import mhtml._
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.html.Input

import scala.scalajs.js.Date
import scala.scalajs.js.timers._
import scala.xml.Node

case class Message(channel: String,
                   user: String,
                   timestamp: Double,
                   text: String)

object Chat {
  var socket: WebSocket = {
    val socket = new dom.WebSocket("ws://localhost:9000/chat")
    socket.onmessage = onMessageReceived
    socket.onclose = onClose
    socket.onerror = onClose
    socket
  }
  val rxMessages: Var[List[Message]] = Var(List())

  def render: Node = {
    val filteredMessages = for {
      messages <- rxMessages
      channel <- Client.rxChannel
    } yield {
      messages
        .filter(message => message.channel == channel)
        .sortBy(message => message.timestamp)
    }

    val input = for {
      channel <- Client.rxChannel
      username <- Client.rxUsername
    } yield {
      def onKeyPress(event: KeyboardEvent): Unit = {
        if (event.keyCode == KeyCode.Enter) {
          val messageBox =
            document.getElementById("message-box").asInstanceOf[Input]
          if (!messageBox.value.isEmpty) {
            sendChatMessage(channel, username, messageBox.value)
            messageBox.value = ""

            setTimeout(1000) {
              val div = document.getElementById("messages")
              div.scrollTop = div.scrollHeight
            }
          }
        }
      }
      <input id="message-box" type="text" placeholder="Send a message.." onkeypress={ onKeyPress _ }/>
    }

    <div class="card-panel grey darken-4" id="chat">
      <p>{ Client.rxChannel } chat </p>
      <div id="messages">
        { filteredMessages.map(_.map(renderMessage)) }
      </div>
      { input }
    </div>
  }

  def onClose(e: Event): Unit = {
    setTimeout(10000) {
      socket = new dom.WebSocket("ws://localhost:9000/chat")
      socket.onmessage = onMessageReceived
      socket.onclose = onClose
      socket.onerror = onClose
    }
  }

  def onMessageReceived(e: MessageEvent): Unit = {
    decode[Message](e.data.toString).foreach { message =>
      rxMessages.update(_ ::: List(message))
    }
  }

  def sendChatMessage(channel: String, username: String, text: String): Unit = {
    val message = Message(channel, username, Date.now, text)
    val data = message.asJson.toString
    socket.send(data)
  }

  def renderMessage(message: Message): Node = {
    <div class="row chat-message" style="padding: 0">
      <div class="col s2 left chat-icon">
        <i class="fas fa-user-circle fa-2x"></i>
      </div>
      <div class="col s10" >
        <div class="chat-user"><b>{ message.user }</b><small>, { new Date(message.timestamp).toLocaleTimeString }</small></div>
        <div class="chat-text">{ message.text }</div>
      </div>
    </div>
  }
}
