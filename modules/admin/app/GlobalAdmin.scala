import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.Future
import play.api.Play.current
import play.api.i18n.{Messages, Lang}
import com.mohiva.play.silhouette.core.{Logger, SecuredSettings}
import controllers.admin.routes

object GlobalAdmin extends GlobalSettings with SecuredSettings with Logger {
	
	// 401 - Unauthorized
    override def onNotAuthenticated(request: RequestHeader, lang: Lang): Option[Future[Result]] = Some { Future.successful {
		Redirect(routes.Auth.signIn)
	}}

	// 403 - Forbidden
    override def onNotAuthorized(request: RequestHeader, lang: Lang): Option[Future[Result]] = Some { Future.successful {
		Redirect(routes.Auth.accessDenied)
	}}
	
	// 404 - page not found error
	override def onHandlerNotFound (request: RequestHeader) = Future.successful {
		NotFound(views.html.admin.errors.onHandlerNotFound(request))
	}
	
	// 500 - internal server error
	override def onError (request: RequestHeader, throwable: Throwable) = Future.successful {
		InternalServerError(views.html.admin.errors.onError(throwable))
	}
	
	// called when a route is found, but it was not possible to bind the request parameters
	override def onBadRequest (request: RequestHeader, error: String) = Future.successful {
		BadRequest("Bad Request: " + error)
	}

}