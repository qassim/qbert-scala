package lib.plugins.Metrolink

import com.typesafe.config.ConfigFactory
import lib.Plugin
import org.json4s._
import org.json4s.native.JsonMethods._
import slack.models.Message
import slack.rtm.SlackRtmClient

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaj.http._

class Metrolink extends Plugin {
  val name = "Metrolink"
  val pluginType = "command"

  private val conf = ConfigFactory.load()

  def action(message: Message, args: String, client: SlackRtmClient) = apiRequest.map(result =>
    args match {
      case "stations" => client.sendMessage(message.channel, getAllLocations(result))
      case _ => client.sendMessage(message.channel, constructResponse(args, result))
    })

  private def apiRequest(): Future[String] = Future {
    val response: HttpResponse[String] =
      Http("https://api.tfgm.com/odata/Metrolinks?")
        .header("Ocp-Apim-Subscription-Key", conf.getString("plugin.metrolink.apikey"))
        .asString
    response.body
  }

  private def constructResponse(args: String, response: String) = {
    val responseList = parseResponse(response, args)
    responseList.map { response =>
      var resultString = ""
      resultString += s"*Trams due to depart from ${response.StationLocation}*\n"
      if (response.Dest0 != "") resultString += s"*Platform*\n ```[${response.Carriages0}] ${response.Dest0} departs in ${response.Wait0} minutes\n"
      if (response.Dest1 != "") resultString += s"[${response.Carriages1}] ${response.Dest1} departs in ${response.Wait1} minutes\n"
      if (response.Dest2 != "") resultString += s"[${response.Carriages2}] ${response.Dest2} departs in ${response.Wait2} minutes\n"
      resultString += "```"
      resultString += s"*Message Board*: ${response.MessageBoard}"
      resultString
    }.mkString("\n")
  }

  private def parseResponse(body: String, location: String) = {
    val trains = parseJson(body)
    val locationString = parseShort(location).getOrElse(location)
    
    trains.value
          .filter(_.StationLocation.toLowerCase == locationString.toLowerCase)
          .filter(_.Dest0.nonEmpty).distinct
  }


  private def parseShort(location: String) = {
    val shorts = Map("deansgate" -> "Deansgate - Castlefield")
    shorts.get(location.toLowerCase)
  }

  private def parseJson(body: String) = {
    implicit val formats = DefaultFormats
    parse(body).extract[MetrolinkAPIModel.Trains]
  }

  private def getAllLocations(body: String) = {
    val trains = parseJson(body)
    val stationList = trains.value.map {
      _.StationLocation
    }.distinct.sorted.mkString(", ")
    s"The following stations are available: ${stationList}"
  }
}
