package lib.plugins.TestPlugin

import com.typesafe.config.Config
import lib.Plugin
import slack.models.Message
import slack.rtm.SlackRtmClient

class TestPlugin(conf: Config) extends Plugin {

  val name = "TestPlugin"
  val pluginType = "command"

  def action(message: Message, args: String, client: SlackRtmClient): Unit =
    client.sendMessage(message.channel, s"This is a test. :smirk:")
}
