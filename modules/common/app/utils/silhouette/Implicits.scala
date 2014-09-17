package utils.silhouette

import com.mohiva.play.silhouette.core.{LoginInfo, Identity}
import com.mohiva.play.silhouette.core.providers.{CredentialsProvider, PasswordInfo}
import com.mohiva.play.silhouette.core.services.AuthInfo
import com.mohiva.play.silhouette.contrib.utils.BCryptPasswordHasher

object Implicits {
	implicit def key2loginInfo (key: String): LoginInfo = LoginInfo(CredentialsProvider.Credentials, key)
	implicit def loginInfo2key (loginInfo: LoginInfo): String = loginInfo.providerKey
	implicit def pwd2passwordInfo (pwd: String): PasswordInfo = PasswordInfo(BCryptPasswordHasher.ID, pwd)
	implicit def passwordInfo2pwd (passwordInfo: PasswordInfo): String = passwordInfo.password
}