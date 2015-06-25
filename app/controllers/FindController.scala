package controllers

import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import service.{BoundingBox, H2ShapeService, LatLng}

import scala.concurrent.ExecutionContext.Implicits.global

class FindController extends Controller {

  val shapeService = new H2ShapeService()

  def index = Action.async {
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
}