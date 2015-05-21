package controllers

import jtscala.WKTHelper
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.db._

import play.api.Play.current
import jtscala.WKTHelper.{ WKTPreparedStatement, WKTString }
import service.{ LatLng, DemoShapeService }

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
    // lat/lng switched bc data in db is messed up
      "lng" -> bigDecimal,
      "lat" -> bigDecimal
    )(LatLng.apply)(LatLng.unapply))

    val latLng = latLngForm.bindFromRequest().get
    shapeService.findByCoordinate(latLng).map(result =>
      Ok(views.html.findbycoordinates(latLng, result))
    )
  }

  def populateDB(): Unit = {
    val filename = "app/assets/testdata"

    val lines = Source.fromFile(filename).getLines
    DB.withConnection { implicit c =>
      while (lines.hasNext) {
        val (_, id, districtName, wkt) = (lines.next, lines.next, lines.next, lines.next)

        val stmt = c.prepareStatement("INSERT INTO SHAPES (id, district_name, shape) VALUES (?, ?, ?);")
        stmt.setInt(1, id.toInt)
        stmt.setString(2, districtName)
        stmt.setWKT(3, wkt)

        stmt.execute()
      }
    }
  }
}