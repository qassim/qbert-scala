package lib.plugins.UrbanDictionary

import java.net.URLEncoder

import com.typesafe.config.Config
import lib.Plugin
import org.json4s._
import org.json4s.native.JsonMethods._
import slack.models.Message
import slack.rtm.SlackRtmClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaj.http.{Http, HttpResponse}

class UrbanDictionary(conf: Config) extends Plugin {
  implicit val formats: DefaultFormats.type = DefaultFormats

  val name = "urban"
  val pluginType = "command"

  def action(message: Message, args: String, client: SlackRtmClient): Unit =
    getUrbanDefinition(args)
      .map(result => {

        println(result)
        val example = result.example match {
            case "" => "No example provided."
            case _ => result.example
          }

        val response =
            s"*Word*: ${result.word}\n" +
            s"*Definition*: ${result.definition}\n" +
            s"*Example*: ${example} \n" +
            s"`${result.permalink}`"

        client.sendMessage(message.channel, response)
      })

  private def getUrbanDefinition(phrase: String) = Future {
    val phraseString = URLEncoder.encode(phrase, "UTF-8")
    val requestURL = s"https://api.urbandictionary.com/v0/define?term=$phraseString"
    val urbanRequest: HttpResponse[String] = Http(requestURL).asString
    println(urbanRequest.body)
    parse(urbanRequest.body)
      .extract[UrbanDictionaryAPIModel.RootObj]
      .list
      .head
  }
}
