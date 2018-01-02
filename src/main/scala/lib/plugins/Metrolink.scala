package lib.plugins

import com.typesafe.config.ConfigFactory
import lib.Plugin

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scalaj.http._
import org.json4s._
import org.json4s.native.JsonMethods._
import slack.models.Message
import slack.rtm.SlackRtmClient

import scala.collection.mutable.ArrayBuffer


case class Trains(value: List[Train])

case class Train(Id: Int,
                 Line: String,
                 StationLocation: String,
                 Direction: String,
                 MessageBoard: String,
                 LastUpdated: String,
                 Wait0: String,
                 Wait1: String,
                 Wait2: String,
                 Dest0: String,
                 Dest1: String,
                 Dest2: String,
                 Carriages0: String,
                 Carriages1: String,
                 Carriages2: String)

class Metrolink extends Plugin {
  private val availableStations = List("Piccadilly", "MediaCityUK", "Bury", "Deansgate - Castlefield", "A load more but I haven't yet normalised the API results -> input")
  private val conf = ConfigFactory.load()

  def name(): String = "Metrolink"
  def pluginType(): String = "command"

  def action(message: Message, args: String, client: SlackRtmClient) = {
    if (args.contains("stations")) {
      client.sendMessage(message.channel, s"The following stations are available (match case for now): ${availableStations.mkString(", ")}")
    } else {
      apiRequest onSuccess {
        case response => filterAndRespond(message.channel, args, response, client)
      }
    }
  }

  private def apiRequest(): Future[String] = Future {
    val response: HttpResponse[String] = Http("https://api.tfgm.com/odata/Metrolinks?").header("Ocp-Apim-Subscription-Key", conf.getString("plugin.metrolink.apikey")).asString
    response.body
  }

  private def filterAndRespond(channel: String, args: String, response: String, client: SlackRtmClient) = {
    val responseList = parseResponse(response, args)
    if (!responseList.isEmpty) {

      val response = responseList(0)
      var boardList = ArrayBuffer[String]()
      if (!response.Dest0.equals("")) boardList += s"[${response.Carriages0}] ${response.Dest0} departs in ${response.Wait0} minutes"
      if (!response.Dest1.equals("")) boardList += s"[${response.Carriages1}] ${response.Dest1} departs in ${response.Wait1} minutes"
      if (!response.Dest2.equals("")) boardList += s"[${response.Carriages2}] ${response.Dest2} departs in ${response.Wait2} minutes"

      if (boardList.isEmpty) {
        client.sendMessage(channel, s"There are no services due to depart from ${response.StationLocation}.")
      } else {
        client.sendMessage(channel, s"Trams due to the depart from ${response.StationLocation}")
        boardList.foreach { message =>
          client.sendMessage(channel, message)
        }
      }

      client.sendMessage(channel, s"Message Board: ${response.MessageBoard}")

    } else {
      client.sendMessage(channel, s"There are no results for ${args}, you may have misspelled the location or there are no trams due.")
    }
  }

  private def parseResponse(body: String, location: String) = {
    implicit val formats = DefaultFormats
    val trains = parse(body).extract[Trains]
    trains.value.filter(_.StationLocation == location)
  }
}

