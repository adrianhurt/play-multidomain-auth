package utils.silhouette

import models.Manager
import Implicits._
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import scala.concurrent.Future

class ManagerService extends IdentityService[Manager] {
  def retrieve(loginInfo: LoginInfo): Future[Option[Manager]] = Manager.findByEmail(loginInfo)
  def save(manager: Manager): Future[Manager] = Manager.save(manager)
}