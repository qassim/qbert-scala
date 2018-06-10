package lib
import java.io.File

import org.clapper.classutil.{ClassFinder, ClassInfo}


object PluginRepository {

  def getPlugins: List[Plugin] = {
    val classpath = List(".").map(new File(_))
    val finder = ClassFinder(classpath)
    val classes = finder.getClasses
    val classMap = ClassFinder.classInfoMap(classes)
    val plugins: List[ClassInfo] = ClassFinder.concreteSubclasses("lib.Plugin", classMap).toList

    plugins.map(toPlugin)
  }

  private def toPlugin(pd: ClassInfo): Plugin = {
    Class.forName(pd.name).newInstance().asInstanceOf[lib.Plugin]
  }
}
