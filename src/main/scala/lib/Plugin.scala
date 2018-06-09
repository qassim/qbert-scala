package lib

import slack.models.Message
import slack.rtm.SlackRtmClient

trait Plugin {
  val name: String
  val pluginType: String
  def action(message: Message, args: String, client: SlackRtmClient)
}
