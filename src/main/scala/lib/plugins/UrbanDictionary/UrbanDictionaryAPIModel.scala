package lib.plugins.UrbanDictionary

object UrbanDictionaryAPIModel {

  case class ListBis(
                      definition: String,
                      permalink: String,
                      thumbs_up: Double,
                      sound_urls: List[String],
                      author: String,
                      word: String,
                      defid: Double,
                      current_vote: String,
                      written_on: String,
                      example: String,
                      thumbs_down: Double
                    )

  case class RootObj(list: List[ListBis])

}