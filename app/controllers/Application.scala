package controllers

import com.vividsolutions.jts.io.{ WKTReader, WKBWriter }
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.db._

import play.api.Play.current
import service.{ Point, DemoShapeService }

import scala.io.Source

import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

  val shapeService = new DemoShapeService()

  def setup = Action {
    populateDB()
    Redirect(routes.Application.index)
  }

  def index() = Action.async {
    shapeService.listAll().map(ls => Ok(views.html.index(ls)))
  }

  def findByCoordinates() = Action.async { implicit request =>
    val latLngForm = Form(mapping(
      "lat" -> bigDecimal,
      "lng" -> bigDecimal
    )(Point.apply)(Point.unapply))

    val latLng = latLngForm.bindFromRequest().get
    shapeService.findByCoordinate(latLng).map(result =>
      Ok(views.html.findbycoordinates(latLng, result))
    )
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