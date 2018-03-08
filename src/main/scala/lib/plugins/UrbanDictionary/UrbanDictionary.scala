package lib.plugins.UrbanDictionary

import java.net.URLEncoder

import lib.Plugin
import org.json4s._
import org.json4s.native.JsonMethods._
import slack.models.Message
import slack.rtm.SlackRtmClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaj.http.{Http, HttpResponse}

class UrbanDictionary extends Plugin {
  implicit val formats = DefaultFormats

  def name: String = "urban"
  def pluginType: String = "command"

  def action(message: Message, args: String, client: SlackRtmClient) = {
    getUrbanDefinition(args).map {result =>
      client.sendMessage(message.channel,
        s"*Word*: ${result.word}\n" +
        s"*Definition*: ${result.definition}\n" +
        s"*Example*: ${result.example match {
          case "" => "No example provided."
          case _ => result.example
        }} \n"+
        s"`${result.permalink}`"
      )
    }
  }

  private def getUrbanDefinition(phrase: String) = Future {
    val phraseString = URLEncoder.encode(phrase, "UTF-8")
    val urbanRequest: HttpResponse[String] =
      Http(s"https://api.urbandictionary.com/v0/define?term=${phraseString}").asString
    parse(urbanRequest.body).extract[UrbanDictionaryAPIModel.RootObj].list(0)
  }
}
