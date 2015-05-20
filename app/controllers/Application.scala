package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def setup = Action {
    // TODO start H2
    // TODO enter testdata

    Redirect(routes.Application.index)
  }

}