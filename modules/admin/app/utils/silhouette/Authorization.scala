package utils.silhouette

import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import models.Manager
import play.api.mvc.Request
import play.api.i18n.Messages
import scala.concurrent.Future

/**
 * Only allows those managers that have at least a role of the selected.
 * Master role is always allowed.
 * Ex: WithRole("high", "sales") => only managers with roles "high" OR "sales" (or "master") are allowed.
 */
case class WithRole(anyOf: String*) extends Authorization[Manager, CookieAuthenticator] {
  def isAuthorized[A](manager: Manager, authenticator: CookieAuthenticator)(implicit r: Request[A]) = Future.successful {
    WithRole.isAuthorized(manager, anyOf: _*)
  }
}
object WithRole {
  def isAuthorized(manager: Manager, anyOf: String*): Boolean =
    anyOf.intersect(manager.roles).size > 0 || manager.roles.contains("master")
}

/**
 * Only allows those managers that have every of the selected roles.
 * Master role is always allowed.
 * Ex: Restrict("high", "sales") => only managers with roles "high" AND "sales" (or "master") are allowed.
 */
case class WithRoles(allOf: String*) extends Authorization[Manager, CookieAuthenticator] {
  def isAuthorized[A](manager: Manager, authenticator: CookieAuthenticator)(implicit r: Request[A]) = Future.successful {
    WithRoles.isAuthorized(manager, allOf: _*)
  }
}
object WithRoles {
  def isAuthorized(manager: Manager, allOf: String*): Boolean =
    allOf.intersect(manager.roles).size == allOf.size || manager.roles.contains("master")
}