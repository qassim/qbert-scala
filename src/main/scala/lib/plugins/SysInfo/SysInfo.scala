package lib.plugins.SysInfo

import lib.Plugin
import slack.models.Message
import slack.rtm.SlackRtmClient
import java.lang.management.ManagementFactory

import com.typesafe.config.{Config, ConfigFactory}

class SysInfo(conf: Config) extends Plugin {
  val name = "sysinfo"
  val pluginType = "command"


  def action(message: Message, args: String, client: SlackRtmClient): Unit = {
    val mb = 1024*1024
    val runtime = Runtime.getRuntime
    val runtimeBean = ManagementFactory.getRuntimeMXBean
    val usedMemory = (runtime.totalMemory - runtime.freeMemory) / mb
    val freeMemory = runtime.freeMemory / mb
    val totalMemory = runtime.totalMemory / mb
    val maxMemory = runtime.maxMemory / mb
    val uptime = runtimeBean.getUptime

    client.sendMessage(message.channel, s"*Memory* | *Used*: ${usedMemory}MB | *Free*: ${freeMemory}MB | *Total*: ${totalMemory}MB | *Max available*: ${maxMemory}MB | *Uptime*: ${(uptime / 1000) / 60} minutes")

  }
}
