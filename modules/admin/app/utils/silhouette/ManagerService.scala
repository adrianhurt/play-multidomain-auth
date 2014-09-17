package utils.silhouette

import models.Manager
import Implicits._
import com.mohiva.play.silhouette.core.LoginInfo
import com.mohiva.play.silhouette.core.services.IdentityService
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._

class ManagerService extends IdentityService[Manager] {
	def retrieve (loginInfo: LoginInfo): Future[Option[Manager]] = Manager.findByEmail(loginInfo)
}