package controllers

import jtscala.WKTHelper.WKTPreparedStatement
import play.api.Play.current
import play.api.data.Forms._
import play.api.data._
import play.api.db._
import play.api.mvc._
import service.{BoundingBox, DemoShapeService, LatLng}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

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
    )(LatLng.apply)(LatLng.unapply))

    val latLng = latLngForm.bindFromRequest().get
    shapeService.findByCoordinate(latLng).map(result =>
      Ok(views.html.findbycoordinates(latLng, result))
    )
  }

  def findByBoundingBox() = Action.async { implicit request =>
    val boundingBoxForm = Form(mapping(
      "upperLeftLat" -> bigDecimal,
      "upperLeftLng" -> bigDecimal,
      "lowerRightLat" -> bigDecimal,
      "lowerRightLng" -> bigDecimal
    )(BoundingBox.apply)(BoundingBox.unapply))

    val boundingBox = boundingBoxForm.bindFromRequest().get
    shapeService.findByBoundingBox(boundingBox).map(result =>
      Ok(views.html.findbyboundingbox(boundingBox, result))
    )
  }

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