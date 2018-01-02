package lib.plugins

import slack.models.Message
import slack.rtm.SlackRtmClient
import scalaj.http._
import org.json4s._
import org.json4s.native.Serialization.{read, write}

import lib.Plugin

class Logger extends Plugin {
  def name(): String = "Logger"

  def action(message: Message, args: String, client: SlackRtmClient) = {
    implicit val formats = DefaultFormats
    Http("http://127.0.0.1:3000/api/submit").postData(write(message)).header("content-type", "application/json").asString.code

  }

  def pluginType(): String = "eventListener"
}
