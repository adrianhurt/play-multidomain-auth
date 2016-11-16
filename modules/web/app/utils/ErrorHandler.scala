package web

import play.api.http.DefaultHttpErrorHandler
import com.mohiva.play.silhouette.api.actions.{ SecuredErrorHandler, UnsecuredErrorHandler }
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.i18n.{ I18nSupport, MessagesApi, Messages }
import play.api.routing.Router
import scala.concurrent.Future
import javax.inject.{ Singleton, Inject, Provider }
import controllers.web.routes

@Singleton
class ErrorHandler @Inject() (
    env: Environment,
    config: Configuration,
    sourceMapper: OptionalSourceMapper,
    router: Provider[Router],
    val messagesApi: MessagesApi
) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) with SecuredErrorHandler with UnsecuredErrorHandler with I18nSupport {

  // 401 - Unauthorized
  override def onNotAuthenticated(implicit request: RequestHeader): Future[Result] = Future.successful {
    Redirect(routes.Auth.signIn)
  }

  // 403 - Forbidden
  override def onNotAuthorized(implicit request: RequestHeader): Future[Result] = Future.successful {
    Forbidden(views.html.web.errors.accessDenied())
  }

  // 404 - page not found error
  override def onNotFound(request: RequestHeader, message: String): Future[Result] = Future.successful {
    NotFound(env.mode match {
      case Mode.Prod => views.html.web.errors.notFound(request)(request2Messages(request))
      case _ => views.html.defaultpages.devNotFound(request.method, request.uri, Some(router.get))
    })
  }

  // 500 - internal server error
  override def onProdServerError(request: RequestHeader, exception: UsefulException) = Future.successful {
    InternalServerError(views.html.web.errors.error(request, exception)(request2Messages(request)))
  }
}