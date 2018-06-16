package lib
import java.io.File
import org.clapper.classutil.{ClassFinder, ClassInfo}
import com.typesafe.config.{ConfigFactory, Config}


object PluginRepository {
  private val conf = ConfigFactory.load()

  def getPlugins: List[Plugin] = {
    val classPath = List(new File("."))
    val classMap = ClassFinder.classInfoMap(ClassFinder(classPath).getClasses)
    val plugins: List[ClassInfo] = ClassFinder.concreteSubclasses("lib.Plugin", classMap).toList

    plugins.map(toPlugin)
  }

  private def toPlugin(pd: ClassInfo): Plugin =
    Class.forName(pd.name)
      .getConstructor(classOf[Config])
      .newInstance(conf: Config)
      .asInstanceOf[lib.Plugin]

}
