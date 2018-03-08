package lib.plugins.SysInfo

import lib.Plugin
import slack.models.Message
import slack.rtm.SlackRtmClient

class SysInfo extends Plugin {
  def name(): String = "sysinfo"
  def pluginType(): String = "command"


  def action(message: Message, args: String, client: SlackRtmClient) = {
    val mb = 1024*1024
    val runtime = Runtime.getRuntime
    val usedMemory = (runtime.totalMemory - runtime.freeMemory) / mb
    val freeMemory = runtime.freeMemory / mb
    val totalMemory = runtime.totalMemory / mb
    val maxMemory = runtime.maxMemory / mb

    client.sendMessage(message.channel, s"*Memory* | *Used*: ${usedMemory}MB | *Free*: ${freeMemory}MB | *Total*: ${totalMemory}MB | *Max available*: ${maxMemory}MB")

  }
}
