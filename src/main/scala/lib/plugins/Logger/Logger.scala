package lib.plugins.Logger

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import lib.{Plugin, SlackAPI}
import org.json4s._
import org.json4s.native.Serialization.write
import slack.models.Message
import slack.rtm.SlackRtmClient

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scalaj.http._


class Logger extends Plugin {
  private implicit val system = ActorSystem("slack")
  private implicit val ec = system.dispatcher
  private implicit val formats = DefaultFormats

  private val api = new SlackAPI().client()
  private val conf = ConfigFactory.load()

  case class MessageLog(message: Message, displayName: String)

  val name =  "Logger"
  val pluginType = "eventListener"

  def action(message: Message, args: String, client: SlackRtmClient) = for {
      user <- api.getUserInfo(message.user)
      code <- writeLog(message, user.name)
    } yield printLog(code, message, user.name)


  private def writeLog(message: Message, displayName: String) = Future {
    val log = MessageLog(message, displayName)
    Http(conf.getString("plugin.logger.endpoint")).postData(write(log))
      .header("content-type", "application/json")
      .header("key", conf.getString("plugin.logger.secret"))
      .asString.code
  }

  private def printLog(code: Int, message: Message, username: String) = code match {
      case 200 => println(s"Logged: <${username}> ${message.text}")
      case _ =>   println(s"Error: Code: ${code} - Message: <${username}> ${message.text}")
    }
}
