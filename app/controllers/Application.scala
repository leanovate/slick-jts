package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.mvc._

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
      "lat" -> bigDecimal,
      "lng" -> bigDecimal
    )(LatLng.apply)(LatLng.unapply))

    val latLng = latLngForm.bindFromRequest().get
    shapeService.findByCoordinate(latLng).map(result =>
      Ok(views.html.findbycoordinates(latLng, result))
    )
  }

  def populateDB(): Unit = {
    val filename = "app/assets/testdata"
    val lines = Source.fromFile(filename).getLines.toList
    shapeService.populate(lines)
  }
}