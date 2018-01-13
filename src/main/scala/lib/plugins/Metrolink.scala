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
                 Carriages2: String,
                 PIDREF: String)

class Metrolink extends Plugin {
  private val conf = ConfigFactory.load()

  def name(): String = "Metrolink"

  def pluginType(): String = "command"

  def action(message: Message, args: String, client: SlackRtmClient) = {
    apiRequest.map { result =>
      args match {
        case "stations" => client.sendMessage(message.channel, getAllLocations(result))
        case _ => client.sendMessage(message.channel, filterAndRespond(args, result))
      }
    }
  }

  private def apiRequest(): Future[String] = Future {
    val response: HttpResponse[String] = Http("https://api.tfgm.com/odata/Metrolinks?").header("Ocp-Apim-Subscription-Key", conf.getString("plugin.metrolink.apikey")).asString
    response.body
  }

  private def filterAndRespond(args: String, response: String) = {
    val responseList = parseResponse(response, args)
    var resultString = ""
    if (!responseList.isEmpty) {
      val response = responseList(0)
      resultString += s"*Trams due to depart from ${response.StationLocation}*\n"

      responseList foreach { response =>
        var boardList = ArrayBuffer[String]()
        if (!response.Dest0.equals("")) boardList += s"[${response.Carriages0}] ${response.Dest0} departs in ${response.Wait0} minutes"
        if (!response.Dest1.equals("")) boardList += s"[${response.Carriages1}] ${response.Dest1} departs in ${response.Wait1} minutes"
        if (!response.Dest2.equals("")) boardList += s"[${response.Carriages2}] ${response.Dest2} departs in ${response.Wait2} minutes"

        if (!boardList.isEmpty) {
          resultString += s"*Platform ${response.PIDREF.takeRight(2)}*\n```${boardList.mkString("\n")}```\n"
        }
      }
      resultString += s"*Message Board*: ${response.MessageBoard}"
      resultString
    } else {
      s"There are no results for ${args}, you may have misspelled the location or there are no trams due."
    }
  }

  private def parseResponse(body: String, location: String) = {
    val trains = parseJson(body)
    val shortSearch = parseShort(location) match {
      case None => ""
      case Some(s) => s
    }
    val search = if (shortSearch.equals("")) location else shortSearch
    trains.value.filter(_.StationLocation.toLowerCase == search.toLowerCase)
  }

  private def parseShort(location: String) = {
    val shorts = Map("deansgate" -> "Deansgate - Castlefield")
    shorts.get(location.toLowerCase)
  }

  private def parseJson(body: String) = {
    implicit val formats = DefaultFormats
    parse(body).extract[Trains]
  }

  private def getAllLocations(body: String) = {
    val trains = parseJson(body)
    val stationList = trains.value.map {_.StationLocation}.distinct.mkString(", ")
    s"The following stations are available: ${stationList}"
  }
}
