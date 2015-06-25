import database.TestDataGenerator
import play.api._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    new TestDataGenerator().populateDB()
  }
}