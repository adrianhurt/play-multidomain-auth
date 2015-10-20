package utils.silhouette

import models.{ User, MailTokenUser }
import play.api.Configuration
import javax.inject.{ Singleton, Inject }

@Singleton
class WebEnvironment @Inject() (val conf: Configuration) extends AuthenticationEnvironment[User, MailTokenUser] {
  val identityService = new UserService
  val passwordInfoDAO = new PasswordInfoWebDAO()
  val tokenService = new MailTokenUserService()
}