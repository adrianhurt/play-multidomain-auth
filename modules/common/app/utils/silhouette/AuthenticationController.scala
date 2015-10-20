package utils.silhouette

import com.mohiva.play.silhouette.api.{ Silhouette, Identity }
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import play.api.i18n.I18nSupport

trait AuthenticationController[I <: Identity, T <: MailToken] extends Silhouette[I, CookieAuthenticator] with I18nSupport {
  def env: AuthenticationEnvironment[I, T]
  implicit def securedRequest2Identity[A](implicit request: SecuredRequest[A]): I = request.identity
  implicit def userAwareRequest2IdentityOpt[A](implicit request: UserAwareRequest[A]): Option[I] = request.identity
}
