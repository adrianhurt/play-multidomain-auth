import play.api.http.DefaultHttpErrorHandler
import com.mohiva.play.silhouette.api.SecuredErrorHandler
import play.api._
import play.api.mvc._
import play.api.i18n.Messages
import play.api.routing.Router
import scala.concurrent.Future
import javax.inject._

class ErrorHandler @Inject() (
    env: Environment,
    config: Configuration,
    sourceMapper: OptionalSourceMapper,
    router: Provider[Router],
    webErrorHandler: web.ErrorHandler,
    adminErrorHandler: admin.ErrorHandler) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) with SecuredErrorHandler {

  /*
	* Gets the subdomain: "admin" o "www"
	*/
  private def getSubdomain(request: RequestHeader) = request.domain.replaceFirst("[\\.]?[^\\.]+[\\.][^\\.]+$", "")

  // 401 - Unauthorized
  override def onNotAuthenticated(request: RequestHeader, messages: Messages) = getSubdomain(request) match {
    case "admin" => adminErrorHandler.onNotAuthenticated(request, messages)
    case _ => webErrorHandler.onNotAuthenticated(request, messages)
  }

  // 403 - Forbidden
  override def onNotAuthorized(request: RequestHeader, messages: Messages) = getSubdomain(request) match {
    case "admin" => adminErrorHandler.onNotAuthorized(request, messages)
    case _ => webErrorHandler.onNotAuthorized(request, messages)
  }

  // 404 - page not found error
  override def onNotFound(request: RequestHeader, message: String) = getSubdomain(request) match {
    case "admin" => adminErrorHandler.onNotFound(request, message)
    case _ => webErrorHandler.onNotFound(request, message)
  }

  // 500 - internal server error
  override def onProdServerError(request: RequestHeader, exception: UsefulException) = getSubdomain(request) match {
    case "admin" => adminErrorHandler.onProdServerError(request, exception)
    case _ => webErrorHandler.onProdServerError(request, exception)
  }

}