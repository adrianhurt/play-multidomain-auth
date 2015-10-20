package utils.silhouette

import models.User
import Implicits._
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import scala.concurrent.Future

class UserService extends IdentityService[User] {
  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = User.findByEmail(loginInfo)
  def save(user: User): Future[User] = User.save(user)
}