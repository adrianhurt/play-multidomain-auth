package utils.silhouette

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }
import Implicits._

trait IdentitySilhouette extends Identity {
  def key: String
  def loginInfo: LoginInfo = key
}