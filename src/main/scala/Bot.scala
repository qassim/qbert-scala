package scala

import akka.actor._
import lib.{PluginExecutor, PluginManager}
import slack.rtm.SlackRtmClient
import com.typesafe.config._

object Main extends App {
  implicit val system = ActorSystem("slack")
  implicit val ec = system.dispatcher

  val conf = ConfigFactory.load()
  val token = conf.getString("slack.apikey")

  val client = SlackRtmClient(token)
  val selfId = client.state.self.id

  val manager = new PluginManager(client)
  var executor = new PluginExecutor(client, manager)

  manager.init()

  client.onMessage { message =>
      executor.exec(message)
  }
}
