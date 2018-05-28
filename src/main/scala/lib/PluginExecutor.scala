package lib

import slack.models.Message
import slack.rtm.SlackRtmClient

class PluginExecutor(client: SlackRtmClient, plugins: List[Plugin]) {

  def exec(message: Message, prefix: String) = {
    val text = message.text
    val command = getCommand(text)
    val commandArgs = getArgs(text)
    val isCommand = text(0).toString.equals(prefix)

    plugins.foreach { plugin =>
        if (isCommand && plugin.name.toUpperCase.equals(command)) {
          plugin.action(message, commandArgs, client)
        } else if (plugin.pluginType.equals("eventListener") && !isCommand) {
          plugin.action(message, commandArgs, client)
        }
    }
  }

  private def getCommand(text: String) = text.split(" ")(0).splitAt(1)._2.toUpperCase
  private def getArgs(text: String) = text.split(" ").splitAt(1)._2.mkString(" ")
}
