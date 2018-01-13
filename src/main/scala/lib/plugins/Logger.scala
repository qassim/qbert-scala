package lib.plugins

import akka.actor.ActorSystem
import slack.models.Message
import slack.rtm.SlackRtmClient
import com.typesafe.config.ConfigFactory

import scala.util.{Failure, Success}
import scala.concurrent.Future
import scalaj.http._
import org.json4s._
import org.json4s.native.Serialization.write
import lib.Plugin
import lib.SlackAPI


class Logger extends Plugin {
  private implicit val system = ActorSystem("slack")
  private implicit val ec = system.dispatcher

  private val conf = ConfigFactory.load()

  case class MessageLog(message: Message, displayName: String)

  def name(): String = "Logger"
  def pluginType(): String = "eventListener"

  def action(message: Message, args: String, client: SlackRtmClient) = {
    val api = new SlackAPI()
    val client = api.client()

    client.getUserInfo(message.user).map { result =>
      writeLog(message, result.name) onComplete {
        case Success(s) => println(s"Log: <${result.name}> ${message.text}")
        case Failure(f) => println(f)
      }
    }
  }

  def writeLog(message: Message, displayName: String) = Future {
    implicit val formats = DefaultFormats
    val log = MessageLog(message, displayName)
    Http(conf.getString("plugin.logger.endpoint")).postData(write(log))
      .header("content-type", "application/json")
      .header("key", conf.getString("plugin.logger.secret"))
      .asString.code
  }
}
