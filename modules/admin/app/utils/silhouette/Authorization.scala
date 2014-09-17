package utils.silhouette

import com.mohiva.play.silhouette.core.Authorization
import models.Manager
import play.api.mvc.RequestHeader
import play.api.i18n.Lang


/**
	Only allows those managers that have at least a role of the selected.
	Master role is always allowed.
	Ex: WithRole("high", "sales") => only managers with roles "high" or "sales" (or "master") are allowed.
*/
case class WithRole (anyOf: String*) extends Authorization[Manager] {
	def isAuthorized (manager: Manager)(implicit request: RequestHeader, lang: Lang) = WithRole.isAuthorized(manager, anyOf:_*)
}
object WithRole {
	def isAuthorized (manager: Manager, anyOf: String*): Boolean =
		anyOf.intersect(manager.roles).size > 0 || manager.roles.contains("master")
}

/**
	Only allows those managers that have every of the selected roles.
	Master role is always allowed.
	Ex: Restrict("high", "sales") => only managers with roles "high" and "sales" (or "master") are allowed.
*/
case class WithRoles (allOf: String*) extends Authorization[Manager] {
	def isAuthorized (manager: Manager)(implicit request: RequestHeader, lang: Lang) = WithRoles.isAuthorized(manager, allOf:_*)
}
object WithRoles {
	def isAuthorized (manager: Manager, allOf: String*): Boolean =
		allOf.intersect(manager.roles).size == allOf.size || manager.roles.contains("master")
}
