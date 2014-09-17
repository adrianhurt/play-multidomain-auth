package utils.silhouette

import models.User
import Implicits._
import com.mohiva.play.silhouette.core.LoginInfo
import com.mohiva.play.silhouette.core.services.IdentityService
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._

class UserService extends IdentityService[User] {
	def retrieve (loginInfo: LoginInfo): Future[Option[User]] = User.findByEmail(loginInfo)
}