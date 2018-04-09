package controllers

import play.api.mvc._

final class ApplicationController()(cc: ControllerComponents)
    extends AbstractController(cc) {

  def index = Action {
    Ok(views.html.index())
  }
}
