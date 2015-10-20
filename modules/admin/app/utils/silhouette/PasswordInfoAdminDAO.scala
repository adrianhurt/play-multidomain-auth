package utils.silhouette

import models.Manager
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.api.LoginInfo
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import Implicits._

class PasswordInfoAdminDAO extends DelegableAuthInfoDAO[PasswordInfo] {

  def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    update(loginInfo, authInfo)

  def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] =
    Manager.findByEmail(loginInfo).map {
      case Some(manager) => Some(manager.password)
      case _ => None
    }

  def remove(loginInfo: LoginInfo): Future[Unit] = Manager.remove(loginInfo)

  def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    find(loginInfo).flatMap {
      case Some(_) => update(loginInfo, authInfo)
      case None => add(loginInfo, authInfo)
    }

  def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    Manager.findByEmail(loginInfo).map {
      case Some(manager) => {
        Manager.save(manager.copy(password = authInfo))
        authInfo
      }
      case _ => throw new Exception("PasswordInfoAdminDAO - update : the manager must exists to update its password")
    }

}