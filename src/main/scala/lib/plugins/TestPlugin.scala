package lib.plugins

import lib.Plugin
import slack.models.Message
import slack.rtm.SlackRtmClient

class TestPlugin extends Plugin {

  def name(): String = "TestPlugin"
  def action(message: Message, args: String, client: SlackRtmClient) = {
    client.sendMessage(message.channel, name)
  }
  def pluginType(): String = "command"
}
