package lib.plugins.Ping

import com.typesafe.config.Config
import lib.Plugin
import slack.models.Message
import slack.rtm.SlackRtmClient


class Ping(conf: Config) extends Plugin {

  val name = "Ping"
  val pluginType = "command"

  def action(message: Message, args: String, client: SlackRtmClient): Unit = client.sendMessage(message.channel, s"pong")

}
