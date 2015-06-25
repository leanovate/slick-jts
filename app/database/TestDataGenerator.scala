package database

import jtscala.WKTHelper.WKTPreparedStatement
import play.api.Play.current
import play.api.db.DB
import scala.io.Source

class TestDataGenerator {
  def populateDB(): Unit = {
    val filename = "app/assets/testdata"

    val lines = Source.fromFile(filename).getLines
    DB.withConnection { implicit c =>
      while (lines.hasNext) {
        val (id, districtName, wkt) = (lines.next, lines.next, lines.next)

        val stmt = c.prepareStatement("INSERT INTO SHAPES (id, district_name, shape) VALUES (?, ?, ?);")
        stmt.setInt(1, id.toInt)
        stmt.setString(2, districtName)
        stmt.setWKT(3, wkt)

        stmt.execute()
      }
    }
  }
}
