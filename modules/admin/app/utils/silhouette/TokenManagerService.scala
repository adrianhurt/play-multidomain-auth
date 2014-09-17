package utils.silhouette

import models.TokenManager
import com.mohiva.play.silhouette.core.services.TokenService
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class TokenManagerService extends TokenService[TokenManager] {
	def create (token: TokenManager): Future[Option[TokenManager]] = {
		TokenManager.save(token).map(Some(_))
	}
	def retrieve (id: String): Future[Option[TokenManager]] = {
		TokenManager.findById(id)
	}
	def consume (id: String): Unit = {
		TokenManager.delete(id)
	}
}