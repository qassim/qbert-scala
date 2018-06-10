package lib

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import slack.api.SlackApiClient

import scala.concurrent.ExecutionContextExecutor


class SlackAPI {
  private val conf = ConfigFactory.load()

  def client(): SlackApiClient = {
    implicit val system: ActorSystem = ActorSystem("slack")
    implicit val ec: ExecutionContextExecutor = system.dispatcher

    SlackApiClient(conf.getString("slack.apikey"))
  }
}
