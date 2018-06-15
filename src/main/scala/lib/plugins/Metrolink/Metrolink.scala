package lib.plugins.Metrolink

import com.typesafe.config.{Config, ConfigFactory}
import lib.Plugin
import org.json4s._
import org.json4s.native.JsonMethods._
import slack.models.Message
import slack.rtm.SlackRtmClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaj.http._

class Metrolink(conf: Config) extends Plugin {
  val name = "Metrolink"
  val pluginType = "command"

  private implicit val formats: DefaultFormats.type = DefaultFormats
  private val shortcuts = Map("deansgate" -> "Deansgate - Castlefield")

  def action(message: Message, args: String, client: SlackRtmClient): Unit = apiRequest().map(result =>
    args match {
      case "stations" => client.sendMessage(message.channel, getAllLocations(result))
      case _ => client.sendMessage(message.channel, constructResponse(args, result))
    })

  private def apiRequest(): Future[String] = Future {
    Http("https://api.tfgm.com/odata/Metrolinks?")
      .header("Ocp-Apim-Subscription-Key", conf.getString("plugin.metrolink.apikey"))
      .asString
      .body
  }

  private def constructResponse(args: String, response: String) = {
    var messageBoardString = ""
    parseResponse(response, args)
      .map(response => {
        var resultString = ""
        resultString += s"*Trams due to depart from ${response.StationLocation}*\n"
        if (response.Dest0 != "") resultString += s"*Platform*\n ```[${response.Carriages0}] ${response.Dest0} departs in ${response.Wait0} minutes\n"
        if (response.Dest1 != "") resultString += s"[${response.Carriages1}] ${response.Dest1} departs in ${response.Wait1} minutes\n"
        if (response.Dest2 != "") resultString += s"[${response.Carriages2}] ${response.Dest2} departs in ${response.Wait2} minutes\n"
        resultString += "```"
        messageBoardString = s" *Message Board*: ${response.MessageBoard}\n"
        resultString
      }
    ).mkString("\n") + messageBoardString
  }

  private def parseResponse(body: String, location: String) = {
    val trains = parseJson(body)
    val locationString = parseShort(location).getOrElse(location)

    trains.value
      .filter(_.StationLocation.toLowerCase == locationString.toLowerCase)
      .filter(_.Dest0.nonEmpty)
      .distinct
  }

  private def parseShort(location: String) = shortcuts.get(location.toLowerCase)
  private def parseJson(body: String) = parse(body).extract[MetrolinkAPIModel.Trains]

  private def getAllLocations(body: String) = {
    val trains = parseJson(body)
    val stationList = trains.value
      .map(_.StationLocation)
      .distinct
      .sorted
      .mkString(", ")
    s"The following stations are available: $stationList"
  }
}
