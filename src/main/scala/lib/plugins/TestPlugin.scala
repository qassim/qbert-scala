package lib.plugins

import lib.Plugin
import slack.rtm.SlackRtmClient

class TestPlugin extends Plugin {

  def name(): String = "TestPlugin"
  def action(channel: String, message: String, client: SlackRtmClient) = {
    client.sendMessage(channel, name)
  }
  def pluginType(): String = "command"
}
