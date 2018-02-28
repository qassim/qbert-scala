package lib.plugins

import java.net.URLEncoder

import lib.Plugin
import slack.rtm.SlackRtmClient
import slack.models.Message

import scalaj.http.{Http, HttpResponse}
import com.typesafe.config.ConfigFactory

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.util.{Failure, Success}


class Weather extends Plugin {
  private val conf = ConfigFactory.load()
  implicit val formats = DefaultFormats

  def name() = "Weather"
  def pluginType() = "command"

  def action(message: Message, args: String, client: SlackRtmClient) = {

  val results = for {
    locationData <- getLocation(args)
    weatherResult <- getWeather(locationData)
  } yield weatherResult

    results.map { result =>
      client.sendMessage(message.channel,
        s"*${result.address}* | " +
        s"*Temperature*: ${result.weather.currently.temperature}°C. | " +
        s"*Currently*: ${result.weather.currently.summary getOrElse "No summary"} | " +
        s"*Humidity*: ${result.weather.currently.humidity * 100}% | " +
        s"*Day*: ${result.weather.hourly.summary getOrElse "No day summary"}")
    }
  }

  def getWeather(locationData: WeatherAPIModel.Results) = Future {
    val weatherRequest: HttpResponse[String] = Http(s"https://api.darksky.net/forecast/${conf.getString("plugin.weather.fcio")}/${locationData.geometry.location.lat},${locationData.geometry.location.lng}?units=uk2").asString
    val weatherData = parse(weatherRequest.body).extract[WeatherAPIModel.RootWeatherJSON]
    WeatherAPIModel.weatherResult(locationData.formatted_address, weatherData)
  }

  def getLocation(location: String) = Future {
    val locationString = URLEncoder.encode(location, "UTF-8")
    val locationRequest: HttpResponse[String] =
      Http(s"https://maps.googleapis.com/maps/api/geocode/json?address=${locationString}&key=${conf.getString("plugin.weather.gmapi")}").asString
    parse(locationRequest.body).extract[WeatherAPIModel.RootLocationJSON].results(0)
  }
}
