package utils.silhouette

import models.User
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.api.LoginInfo
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import Implicits._

class PasswordInfoWebDAO extends DelegableAuthInfoDAO[PasswordInfo] {

  def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    update(loginInfo, authInfo)

  def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] =
    User.findByEmail(loginInfo).map {
      case Some(user) if user.emailConfirmed => Some(user.password)
      case _ => None
    }

  def remove(loginInfo: LoginInfo): Future[Unit] = User.remove(loginInfo)

  def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    find(loginInfo).flatMap {
      case Some(_) => update(loginInfo, authInfo)
      case None => add(loginInfo, authInfo)
    }

  def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    User.findByEmail(loginInfo).map {
      case Some(user) => {
        User.save(user.copy(password = authInfo))
        authInfo
      }
      case _ => throw new Exception("PasswordInfoWebDAO - update : the user must exists to update its password")
    }

}