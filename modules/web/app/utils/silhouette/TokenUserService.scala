package utils.silhouette

import models.TokenUser
import com.mohiva.play.silhouette.core.services.TokenService
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class TokenUserService extends TokenService[TokenUser] {
	def create (token: TokenUser): Future[Option[TokenUser]] = {
		TokenUser.save(token).map(Some(_))
	}
	def retrieve (id: String): Future[Option[TokenUser]] = {
		TokenUser.findById(id)
	}
	def consume (id: String): Unit = {
		TokenUser.delete(id)
	}
}