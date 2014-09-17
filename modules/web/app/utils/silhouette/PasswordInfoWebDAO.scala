package utils.silhouette

import models.User
import Implicits._
import com.mohiva.play.silhouette.core.providers.PasswordInfo
import com.mohiva.play.silhouette.contrib.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.core.LoginInfo
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class PasswordInfoWebDAO extends DelegableAuthInfoDAO[PasswordInfo] {

	def save (loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
		User.findByEmail(loginInfo).map { maybeUser =>
			maybeUser.map { user =>
				User.save(user.copy(password = authInfo))
			}
			authInfo
		}

	def find (loginInfo: LoginInfo): Future[Option[PasswordInfo]] =
		User.findByEmail(loginInfo).map {
			case Some(user) if user.emailConfirmed => Some(user.password)
			case _ => None
		}
}