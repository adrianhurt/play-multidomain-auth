package utils.silhouette

import models.Manager
import Implicits._
import com.mohiva.play.silhouette.core.providers.PasswordInfo
import com.mohiva.play.silhouette.contrib.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.core.LoginInfo
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class PasswordInfoAdminDAO extends DelegableAuthInfoDAO[PasswordInfo] {

	def save (loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
		Manager.findByEmail(loginInfo).map { maybeManager =>
			maybeManager.map { manager =>
				Manager.save(manager.copy(password = authInfo))
			}
			authInfo
		}

	def find (loginInfo: LoginInfo): Future[Option[PasswordInfo]] =
		Manager.findByEmailMap(loginInfo) { manager => manager.password }
}