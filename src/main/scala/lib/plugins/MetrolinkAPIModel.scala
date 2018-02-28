package lib.plugins

object MetrolinkAPIModel {
  case class Trains(value: List[Train])

  case class Train(Line: String,
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
}
