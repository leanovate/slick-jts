package controllers

import com.vividsolutions.jts.io.{ WKTReader, WKBWriter }
import play.api.mvc._
import play.api.db._

import play.api.Play.current
import service.DemoShapeService

import scala.io.Source

import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

  val shapeService = new DemoShapeService()

  def index() = Action.async {
    shapeService.listAll().map(ls => Ok(views.html.index(ls)))
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