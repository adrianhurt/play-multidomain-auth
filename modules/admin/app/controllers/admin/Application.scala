package controllers.admin

import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import models._
import utils.admin.Mailer
import utils.silhouette._
import scala.concurrent.Future

object Application extends SilhouetteAdminController {

	def index = SecuredAction.async { implicit request =>
		Future.successful(Ok(views.html.admin.index(request.identity)))
	}
	
	def social = SecuredAction(WithRole("social")).async { implicit request =>
		Future.successful(Ok(views.html.admin.social(request.identity)))
	}
	def salesOrHigh = SecuredAction(WithRole("sales", "high")).async { implicit request =>
		Future.successful(Ok(views.html.admin.salesOrHigh(request.identity)))
	}
	def salesAndHigh = SecuredAction(WithRoles("sales", "high")).async { implicit request =>
		Future.successful(Ok(views.html.admin.salesAndHigh(request.identity)))
	}
	def admin = SecuredAction(WithRole("master")).async { implicit request =>
		Future.successful(Ok(views.html.admin.admin(request.identity)))
	}

}