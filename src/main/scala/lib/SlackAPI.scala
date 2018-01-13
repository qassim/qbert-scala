package lib

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import slack.api.SlackApiClient


class SlackAPI {
  private val conf = ConfigFactory.load()

  def client(): SlackApiClient = {
    implicit val system = ActorSystem("slack")
    implicit val ec = system.dispatcher

    SlackApiClient(conf.getString("slack.apikey"))
  }
}
