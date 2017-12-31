package lib

import java.io.File

import org.clapper.classutil.ClassFinder
import slack.rtm.SlackRtmClient

import scala.collection.mutable.ListBuffer

class PluginManager(client: SlackRtmClient) {

  private var pluginList = new ListBuffer[Plugin]
  case class Plugin(name: String, path: String, pluginType: String)

  def getPlugins(): ListBuffer[Plugin] = pluginList


  def init() {
    val classpath = List(".").map(new File(_))
    val finder = ClassFinder(classpath)
    val classes = finder.getClasses
    val classMap = ClassFinder.classInfoMap(classes)
    val plugins = ClassFinder.concreteSubclasses("lib.Plugin", classMap)

    plugins.foreach {
      pluginString =>
        val plugin = Class.forName(pluginString.name).newInstance().asInstanceOf[lib.Plugin]
        pluginList += new Plugin(plugin.name, pluginString.name, plugin.pluginType)
    }
  }
  
  def getPlugin(path: String) = {
    if (pluginList.isEmpty) init
    Class.forName(path).newInstance().asInstanceOf[lib.Plugin]
  }

}

