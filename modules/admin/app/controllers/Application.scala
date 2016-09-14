package controllers.admin

import models._
import utils.silhouette._
import com.mohiva.play.silhouette.api.Silhouette
import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.i18n.{ MessagesApi, Messages, Lang }
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import javax.inject.{ Inject, Singleton }

@Singleton
class Application @Inject() (val silhouette: Silhouette[MyEnv[Manager]], val messagesApi: MessagesApi) extends AdminController {

  def index = SecuredAction { implicit request =>
    Ok(views.html.admin.index())
  }

  def myAccount = SecuredAction { implicit request =>
    Ok(views.html.admin.myAccount())
  }

  // REQUIRED ROLES: social (or master)
  def social = SecuredAction(WithRole("social")) { implicit request =>
    Ok(views.html.admin.social())
  }

  // REQUIRED ROLES: sales OR high (or master)
  def salesOrHigh = SecuredAction(WithRole("sales", "high")) { implicit request =>
    Ok(views.html.admin.salesOrHigh())
  }

  // REQUIRED ROLES: sales AND high (or master)
  def salesAndHigh = SecuredAction(WithRoles("sales", "high")) { implicit request =>
    Ok(views.html.admin.salesAndHigh())
  }

  // REQUIRED ROLES: master
  def settings = SecuredAction(WithRole("master")) { implicit request =>
    Ok(views.html.admin.settings())
  }

  def selectLang(lang: String) = Action { implicit request =>
    Logger.logger.debug("Change user lang to : " + lang)
    request.headers.get(REFERER).map { referer =>
      Redirect(referer).withLang(Lang(lang))
    }.getOrElse {
      Redirect(routes.Application.index).withLang(Lang(lang))
    }
  }

}