package scala

import akka.actor._
import lib.{PluginExecutor, PluginRepository}
import slack.rtm.SlackRtmClient
import com.typesafe.config._

import scala.concurrent.ExecutionContextExecutor

object Main extends App {
  implicit val system: ActorSystem = ActorSystem("slack")
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val conf = ConfigFactory.load()
  val token = conf.getString("slack.apikey")
  val prefix = conf.getString("command.prefix")

  val client = SlackRtmClient(token)
  val selfId = client.state.self.id

  val plugins = PluginRepository.getPlugins
  val executor = new PluginExecutor(client, plugins)

  client.onMessage { message =>
      executor.exec(message, prefix)
  }
}
