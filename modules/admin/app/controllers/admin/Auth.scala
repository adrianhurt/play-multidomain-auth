package controllers.admin

import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import models._
import utils.silhouette._
import utils.silhouette.Implicits._
import com.mohiva.play.silhouette.core.{LoginEvent, LogoutEvent, LoginInfo}
import com.mohiva.play.silhouette.core.providers.Credentials
import com.mohiva.play.silhouette.core.exceptions.{AuthenticationException, AccessDeniedException}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Auth extends SilhouetteAdminController {

	// SIGN IN
	
	val signInForm = Form(
		mapping(
			"identifier" -> email,
			"password" -> nonEmptyText
		)(Credentials.apply)(Credentials.unapply)
	)
	
	/**
	* Starts the sign in mechanism. It shows the login form.
	*/
    def signIn = UserAwareAction.async { implicit request =>
		Future.successful( request.identity match {
			case Some(manager) => Redirect(routes.Application.index)
			case None => Ok(views.html.admin.auth.signIn(signInForm))
		})
    }
	
	/**
	* Authenticates the manager based on his email and password
	*/
	def authenticate = Action.async { implicit request =>
		signInForm.bindFromRequest.fold(
			formWithErrors => Future.successful(BadRequest(views.html.admin.auth.signIn(formWithErrors))),
			credentials => {
				credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
					identityService.retrieve(loginInfo).flatMap {
						case Some(manager) => authenticatorService.create(manager).flatMap { authenticator =>
							eventBus.publish(LoginEvent(manager, request, request2lang))
							authenticatorService.init(authenticator, Future.successful(Redirect(routes.Application.index)))
						}
						case None => Future.failed(new AuthenticationException("Couldn't find manager"))
					}
				}.recoverWith {
					case e: AccessDeniedException => Future.successful(Redirect(routes.Auth.signIn).flashing("error" -> Messages("access.credentials.incorrect")))
				}.recoverWith(exceptionHandler)
			}
		)
	}
	
	
	// SIGN OUT
	
	/**
	* Signs out the manager
	*/
	def signOut = SecuredAction.async { implicit request =>
		eventBus.publish(LogoutEvent(request.identity, request, request2lang))
		authenticatorService.retrieve.flatMap {
			case Some(authenticator) => authenticatorService.discard(authenticator, Future.successful(Redirect(routes.Application.index)))
			case None => Future.failed(new AuthenticationException("Couldn't find authenticator"))
		}
	}
	
	/**
	* Shows an error page when the manager tries to get to an area without the necessary roles.
	*/
	def accessDenied = SecuredAction.async { implicit request =>
		Future.successful(Ok(views.html.admin.auth.accessDenied(request.identity, request)))
	}
	
}