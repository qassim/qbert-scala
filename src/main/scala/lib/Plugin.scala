package lib

import slack.rtm.SlackRtmClient

trait Plugin {
  def name: String
  def action(channel: String, message: String, client: SlackRtmClient)
  def pluginType: String
}
