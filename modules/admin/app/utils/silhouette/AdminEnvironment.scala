package utils.silhouette

import models.{ Manager, MailTokenManager }
import play.api.Configuration
import javax.inject.{ Singleton, Inject }

@Singleton
class AdminEnvironment @Inject() (val conf: Configuration) extends AuthenticationEnvironment[Manager, MailTokenManager] {
  val identityService = new ManagerService
  val passwordInfoDAO = new PasswordInfoAdminDAO()
  val tokenService = new MailTokenManagerService()
}