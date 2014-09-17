package controllers.web

import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import models._
import utils.silhouette._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


object Application extends SilhouetteWebController {

	def index = UserAwareAction.async { implicit request =>
		Future.successful(Ok(views.html.web.index(request.identity)))
	}
	
	def myAccount = SecuredAction.async { implicit request =>
		Future.successful(Ok(views.html.web.myAccount(request.identity)))
	}

}