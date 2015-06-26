import com.softwaremill.macwire.Macwire
import database.TestDataGenerator
import play.api._

object Global extends GlobalSettings with Macwire{

  override def onStart(app: Application) {
    new TestDataGenerator().populateDBIfNecessary()
  }
  val wired = wiredInModule(Application)

  override def getControllerInstance[A](controllerClass: Class[A]) = wired.lookupSingleOrThrow(controllerClass)
}