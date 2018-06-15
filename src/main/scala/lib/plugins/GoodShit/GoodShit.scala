package lib.plugins.GoodShit

import com.typesafe.config.{Config, ConfigFactory}
import lib.Plugin
import slack.models.Message
import slack.rtm.SlackRtmClient

class GoodShit(conf: Config) extends Plugin {

  val name = "goodshit"
  val pluginType = "command"

  def action(message: Message, args: String, client: SlackRtmClient): Unit = client.sendMessage(message.channel,
    "👌👀👌👀👌👀👌👀👌👀 good shit go౦ԁ sHit👌 thats ✔ some good👌👌shit right👌👌there👌👌👌 right✔there ✔✔if i do ƽaү so my self 💯 i say so 💯 thats what im talking about right there right there (chorus: ʳᶦᵍʰᵗ ᵗʰᵉʳᵉ) mMMMMᎷМ💯 👌👌 👌НO0ОଠOOOOOОଠଠOoooᵒᵒᵒᵒᵒᵒᵒᵒᵒ👌 👌👌 👌 💯 👌 👀 👀 👀 👌👌Good shit")
}
