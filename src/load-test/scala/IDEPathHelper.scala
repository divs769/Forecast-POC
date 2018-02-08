import java.nio.file.Path
import io.gatling.commons.util.PathHelper._

object IDEPathHelper {

  val gatlingConfUrl: Path = getClass.getClassLoader.getResource("gatling.conf").toURI
  val projectRootDir = gatlingConfUrl.ancestor(4)

  val mavenSourcesDirectory = projectRootDir / "src" / "load-test" / "scala"
  val mavenResourcesDirectory = projectRootDir / "src" / "load-test" / "resources"
  val mavenTargetDirectory = projectRootDir / "build"
  val mavenBinariesDirectory = mavenTargetDirectory / "classes" / "loadTest"

  val recorderOutputDirectory = mavenSourcesDirectory
  val resultsDirectory = mavenTargetDirectory / "gatling"

  val recorderConfigFile = mavenResourcesDirectory / "recorder.conf"
}
