package lib

import com.typesafe.config.ConfigFactory
import slack.rtm.SlackRtmClient

class PluginExecutor(client: SlackRtmClient, pluginManager: PluginManager) {

  def exec(channel: String, text: String) = {
    val command = getCommand(text)
    val commandArgs = getArgs(text)
    val conf = ConfigFactory.load()
    val isCommand = text(0).toString.equals(conf.getString("command.prefix"))

    pluginManager.getPlugins.foreach { pclass =>
        if (isCommand && pclass.name.toUpperCase.equals(command)) {
          pluginManager.getPlugin(pclass.path).action(channel, commandArgs, client)
        } else if (pclass.pluginType.equals("eventListener") && !isCommand) {
          pluginManager.getPlugin(pclass.path).action(channel, commandArgs, client)
        }
    }
  }

  private def getCommand(text: String) = text.split(" ")(0).splitAt(1)._2.toUpperCase
  private def getArgs(text: String) = text.split(" ").splitAt(1)._2.mkString(" ")
}
