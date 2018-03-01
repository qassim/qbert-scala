package lib.plugins

import java.net.URLEncoder

import lib.Plugin
import slack.rtm.SlackRtmClient
import slack.models.Message
import scalaj.http.{Http, HttpResponse}
import com.typesafe.config.ConfigFactory
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.json4s._
import org.json4s.native.JsonMethods._



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
        s"*Currently*: ${result.weather.currently.summary getOrElse "No summary"} ${result.weather.currently.icon match {
          case "rain" => ":rain_cloud:"
          case "snow" => ":snow_cloud:"
          case "clear-day" => ":sunny:"
          case "clear-night" => ":sunny: :moon:"
          case "sleet" => ":snow_cloud:"
          case "wind" => ":wind_blowing_face:"
          case "fog" => ":fog:"
          case "cloudy" => ":cloud:"
          case "partly-cloudy-day" => ":partly_sunny:"
          case "partly-cloudy-night" => ":partly_sunny: :moon:"
          case _ => ""
        }} | " +
        s"*Temperature*: ${result.weather.currently.temperature}°C. *Feels like*: ${result.weather.currently.apparentTemperature}°C | " +
        s"*Humidity*: ${result.weather.currently.humidity * 100}% | " +
        s"*Day*: ${result.weather.hourly.summary getOrElse "No day summary"}")
    }
  }

  private def getWeather(locationData: WeatherAPIModel.Results) = Future {
    val weatherRequest: HttpResponse[String] = Http(s"https://api.darksky.net/forecast/${conf.getString("plugin.weather.fcio")}/${locationData.geometry.location.lat},${locationData.geometry.location.lng}?units=uk2").asString
    val weatherData = parse(weatherRequest.body).extract[WeatherAPIModel.RootWeatherJSON]
    WeatherAPIModel.weatherResult(locationData.formatted_address, weatherData)
  }

  private def getLocation(location: String) = Future {
    val locationString = URLEncoder.encode(location, "UTF-8")
    val locationRequest: HttpResponse[String] =
      Http(s"https://maps.googleapis.com/maps/api/geocode/json?address=${locationString}&key=${conf.getString("plugin.weather.gmapi")}").asString
    parse(locationRequest.body).extract[WeatherAPIModel.RootLocationJSON].results(0)
  }


}
