package controllers.web

import models._
import utils.silhouette._
import com.mohiva.play.silhouette.api.Silhouette
import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.i18n.{ I18nSupport, MessagesApi, Messages, Lang }
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import javax.inject.{ Inject, Singleton }

@Singleton
class Application @Inject() (conf: Configuration, val silhouette: Silhouette[MyEnv[User]]) extends WebController with I18nSupport {

  def index = UserAwareAction { implicit request =>
    Ok(views.html.web.index())
  }

  def myAccount = SecuredAction { implicit request =>
    Ok(views.html.web.myAccount())
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