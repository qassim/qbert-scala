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

import scala.util.{Failure, Success}


class Weather extends Plugin {
  private val conf = ConfigFactory.load()

  case class Address_components(
                                 long_name: String,
                                 short_name: String,
                                 types: List[String]
                               )
  case class Location(
                       lat: Double,
                       lng: Double
                     )
  case class Viewport(
                       northeast: Location,
                       southwest: Location
                     )
  case class Geometry(
                       location: Location,
                       location_type: String,
                       viewport: Viewport
                     )
  case class Results(
                      address_components: List[Address_components],
                      formatted_address: String,
                      geometry: Geometry,
                      place_id: String,
                      types: List[String]
                    )
  case class RootLocationJSON(
                             results: List[Results],
                             status: String
                           )


  case class Currently(time: Double, summary: Option[String], icon: String, nearestStormDistance: Option[Double], nearestStormBearing: Option[Double], precipIntensity: Double, precipProbability: Double, temperature: Double,
                        apparentTemperature: Double, dewPoint: Double, humidity: Double, pressure: Double, windSpeed: Double, windGust: Double, windBearing: Double, cloudCover: Double, uvIndex: Double, visibility: Double, ozone: Double)
  case class Data(time: Double, precipIntensity: Double, precipProbability: Double)
  case class Minutely(summary: Option[String], data: List[Data])
  case class Flags(sources: List[String], `isd-stations`: List[String], units: String)
  case class RootWeatherJSON(latitude: Double, longitude: Double, timezone: String, currently: Currently, minutely: Minutely, hourly: Minutely, daily: Minutely, flags: Flags, offset: Double)

  case class weatherResult(address: String, weather: RootWeatherJSON)

  def name() = "Weather"
  def pluginType() = "command"

  def action(message: Message, args: String, client: SlackRtmClient) = {

    getWeather(args).map { result =>
      client.sendMessage(message.channel, s"*${result.address}* | *Temperature*: ${result.weather.currently.temperature}°C. | *Currently*: ${result.weather.currently.summary} | *Humidity*: ${result.weather.currently.humidity * 100}% | *Day*: ${result.weather.hourly.summary}")
    }
  }

  def getWeather(location: String) = Future {
    implicit val formats = DefaultFormats

    val locationString = URLEncoder.encode(location, "UTF-8")
    val locationRequest: HttpResponse[String] = Http(s"https://maps.googleapis.com/maps/api/geocode/json?address=${locationString}&key=${conf.getString("plugin.weather.gmapi")}").asString
    val locationData = parse(locationRequest.body).extract[RootLocationJSON].results(0)

    val weatherRequest: HttpResponse[String] = Http(s"https://api.darksky.net/forecast/${conf.getString("plugin.weather.fcio")}/${locationData.geometry.location.lat},${locationData.geometry.location.lng}?units=uk2").asString
    val weatherData = parse(weatherRequest.body).extract[RootWeatherJSON]
    weatherResult(locationData.formatted_address, weatherData)

  }
}
