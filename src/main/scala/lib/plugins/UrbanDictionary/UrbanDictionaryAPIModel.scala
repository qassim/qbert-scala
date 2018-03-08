package lib.plugins.UrbanDictionary

object UrbanDictionaryAPIModel {
  case class ListBis(definition: String,
                      permalink: String,
                      thumbs_up: Double,
                      author: String,
                      word: String,
                      defid: Double,
                      current_vote: String,
                      example: String,
                      thumbs_down: Double)
  case class RootObj(tags: List[String], result_type: String, list: List[ListBis])
}
