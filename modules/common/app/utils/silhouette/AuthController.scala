package utils.silhouette

import play.api.mvc.InjectedController
import play.api.i18n.I18nSupport
import com.mohiva.play.silhouette.api.{ Silhouette, Environment, Identity }
import com.mohiva.play.silhouette.api.actions._

trait AuthController[I <: Identity] extends InjectedController with I18nSupport {
  def silhouette: Silhouette[MyEnv[I]]
  def env: Environment[MyEnv[I]] = silhouette.env

  def SecuredAction = silhouette.SecuredAction
  def UnsecuredAction = silhouette.UnsecuredAction
  def UserAwareAction = silhouette.UserAwareAction

  implicit def securedRequest2User[A](implicit request: SecuredRequest[MyEnv[I], A]): I = request.identity
  implicit def userAwareRequest2UserOpt[A](implicit request: UserAwareRequest[MyEnv[I], A]): Option[I] = request.identity
}