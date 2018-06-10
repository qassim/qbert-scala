package lib.plugins.TestPlugin

import lib.Plugin
import slack.models.Message
import slack.rtm.SlackRtmClient

class TestPlugin extends Plugin {

  val name = "TestPlugin"
  val pluginType = "command"

  def action(message: Message, args: String, client: SlackRtmClient) = client.sendMessage(message.channel, "This is a test. :smirk:")
}
