package utils.silhouette

import models.{Manager, TokenManager}
import com.mohiva.play.silhouette.core.Environment
import com.mohiva.play.silhouette.contrib.authenticators.CookieAuthenticator

trait SilhouetteAdminController extends SilhouetteController[Manager, TokenManager] {
	
	lazy val identityService = new ManagerService
	lazy val passwordInfoDAO = new PasswordInfoAdminDAO
	lazy val tokenService = new TokenManagerService

	implicit lazy val env = Environment[Manager, CookieAuthenticator](
		identityService,
		authenticatorService,
		Map(credentialsProvider.id -> credentialsProvider),
		eventBus
	)
}