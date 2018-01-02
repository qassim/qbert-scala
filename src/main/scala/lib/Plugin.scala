package lib

import slack.models.Message
import slack.rtm.SlackRtmClient

trait Plugin {
  def name: String
  def action(message: Message, args: String, client: SlackRtmClient)
  def pluginType: String
}
