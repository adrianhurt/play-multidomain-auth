import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.Future
import play.api.Play.current
import play.api.i18n.{Messages, Lang}
import com.mohiva.play.silhouette.core.{Logger, SecuredSettings}
import controllers.web.routes

object GlobalWeb extends GlobalSettings with SecuredSettings with Logger {	
	
	// 401 - Unauthorized
	override def onNotAuthenticated(request: RequestHeader, lang: Lang): Option[Future[Result]] = Some { Future.successful {
		Redirect(routes.Auth.signIn)
	}}

	// 403 - Forbidden
	override def onNotAuthorized(request: RequestHeader, lang: Lang): Option[Future[Result]] = Some { Future.successful {
		Redirect(routes.Auth.signIn).flashing("error" -> Messages("access.denied"))
	}}
	
	// 404 - page not found error
	override def onHandlerNotFound (request: RequestHeader) = Future.successful {
		NotFound(views.html.web.errors.onHandlerNotFound(request))
	}
	
	// 500 - internal server error
	override def onError (request: RequestHeader, throwable: Throwable) = Future.successful {
		InternalServerError(views.html.web.errors.onError(throwable))
	}
	
	// called when a route is found, but it was not possible to bind the request parameters
	override def onBadRequest (request: RequestHeader, error: String) = Future.successful {
		BadRequest("Bad Request: " + error)
	}

}