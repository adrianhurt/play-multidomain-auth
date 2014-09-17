package utils.silhouette

import models.{User, TokenUser}
import com.mohiva.play.silhouette.core.Environment
import com.mohiva.play.silhouette.contrib.authenticators.CookieAuthenticator

trait SilhouetteWebController extends SilhouetteController[User, TokenUser] {
	
	lazy val identityService = new UserService
	lazy val passwordInfoDAO = new PasswordInfoWebDAO
	lazy val tokenService = new TokenUserService
	
	implicit lazy val env = Environment[User, CookieAuthenticator](
		identityService,
		authenticatorService,
		Map(credentialsProvider.id -> credentialsProvider),
		eventBus
	)
}