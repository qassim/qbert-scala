package lib.plugins

object WeatherAPIModel {
  case class Address_components(long_name: String, short_name: String, types: List[String])
  case class Location(lat: Double, lng: Double)
  case class Viewport(northeast: Location, southwest: Location)
  case class Geometry(location: Location,
                      location_type: String, viewport: Viewport)
  case class Results(address_components: List[Address_components], formatted_address: String, geometry: Geometry, place_id: String, types: List[String])
  case class RootLocationJSON(results: List[Results], status: String)

  case class Currently(time: Double, summary: Option[String], icon: String, nearestStormDistance: Option[Double], nearestStormBearing: Option[Double], precipIntensity: Double, precipProbability: Double, temperature: Double,
                       apparentTemperature: Double, dewPoint: Double, humidity: Double, pressure: Double, windSpeed: Double, windGust: Double, windBearing: Double, cloudCover: Double, uvIndex: Double, visibility: Double, ozone: Double)

  case class Data(time: Double, precipIntensity: Double, precipProbability: Double)
  case class Minutely(summary: Option[String], data: List[Data])
  case class Flags(sources: List[String], `isd-stations`: List[String], units: String)
  case class RootWeatherJSON(latitude: Double, longitude: Double, timezone: String, currently: Currently, minutely: Minutely, hourly: Minutely, daily: Minutely, flags: Flags, offset: Double)

  case class weatherResult(address: String, weather: RootWeatherJSON)
}
