package lib.plugins.TestPlugin

import lib.Plugin
import slack.models.Message
import slack.rtm.SlackRtmClient

class GoodShit extends Plugin {

  def name(): String = "goodshit"
  def action(message: Message, args: String, client: SlackRtmClient) = {
    val goodshit = "👌👀👌👀👌👀👌👀👌👀 good shit go౦ԁ sHit👌 thats ✔ some good👌👌shit right👌👌there👌👌👌 right✔there ✔✔if i do ƽaү so my self 💯 i say so 💯 thats what im talking about right there right there (chorus: ʳᶦᵍʰᵗ ᵗʰᵉʳᵉ) mMMMMᎷМ💯 👌👌 👌НO0ОଠOOOOOОଠଠOoooᵒᵒᵒᵒᵒᵒᵒᵒᵒ👌 👌👌 👌 💯 👌 👀 👀 👀 👌👌Good shit"
    client.sendMessage(message.channel, goodshit)
  }
  def pluginType(): String = "command"
}
