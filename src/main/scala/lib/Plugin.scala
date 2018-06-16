package lib

import slack.models.Message
import slack.rtm.SlackRtmClient
import com.typesafe.config.Config

trait Plugin {
  val conf: Config
  val name: String
  val pluginType: String
  def action(message: Message, args: String, client: SlackRtmClient)
}
