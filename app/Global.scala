import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.Future
import play.api.Play.current
import play.api.i18n.Lang
import com.mohiva.play.silhouette.core.{Logger, SecuredSettings}


object Global extends GlobalSettings with SecuredSettings with Logger {
	
	private def getSubdomain (request: RequestHeader) = request.domain.replaceFirst("[\\.]?[^\\.]+[\\.][^\\.]+$", "")
	
	
	override def onRouteRequest (request: RequestHeader) = getSubdomain(request) match {
		case "admin" => admin.Routes.routes.lift(request)
		case _ => web.Routes.routes.lift(request)
	}
	
	
	// 401 - Unauthorized
    override def onNotAuthenticated (request: RequestHeader, lang: Lang): Option[Future[Result]] = getSubdomain(request) match {
		case "admin" => GlobalAdmin.onNotAuthenticated(request, lang)
		case _ => GlobalWeb.onNotAuthenticated(request, lang)
	}

	// 403 - Forbidden
    override def onNotAuthorized (request: RequestHeader, lang: Lang): Option[Future[Result]] = getSubdomain(request) match {
		case "admin" => GlobalAdmin.onNotAuthorized(request, lang)
		case _ => GlobalWeb.onNotAuthorized(request, lang)
	}
	
	// 404 - page not found error
	override def onHandlerNotFound (request: RequestHeader) = getSubdomain(request) match {
		case "admin" => GlobalAdmin.onHandlerNotFound(request)
		case _ => GlobalWeb.onHandlerNotFound(request)
	}
	
	// 500 - internal server error
	override def onError (request: RequestHeader, throwable: Throwable) = getSubdomain(request) match {
		case "admin" => GlobalAdmin.onError(request, throwable)
		case _ => GlobalWeb.onError(request, throwable)
	}
	
	// called when a route is found, but it was not possible to bind the request parameters
	override def onBadRequest (request: RequestHeader, error: String) = getSubdomain(request) match {
		case "admin" => GlobalAdmin.onBadRequest(request, error)
		case _ => GlobalWeb.onBadRequest(request, error)
	}

}