package controllers

import com.vividsolutions.jts.io.{ WKTReader, WKBWriter }
import play.api.mvc._
import play.api.db._

import play.api.Play.current

import scala.io.Source

object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def setup = Action {
    populateDB()
    Redirect(routes.Application.index)
  }

  private def populateDB(): Unit = {
    val filename = "app/assets/testdata"

    val lines = Source.fromFile(filename).getLines
    DB.withConnection { implicit c =>
      while (lines.hasNext) {
        val (_, id, districtName, wkt) = (lines.next, lines.next, lines.next, lines.next)

        val shape = new WKBWriter().write(new WKTReader().read(wkt))
        val stmt = c.prepareStatement("INSERT INTO SHAPES (id, district_name, shape) VALUES (?, ?, ?);")
        stmt.setInt(1, id.toInt)
        stmt.setString(2, districtName)
        stmt.setBytes(3, shape)

        stmt.execute()
      }
    }
  }
}