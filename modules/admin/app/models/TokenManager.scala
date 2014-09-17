package models

import com.mohiva.play.silhouette.core.Token
import org.joda.time.DateTime
import scala.concurrent.Future

case class TokenManager (id: String, email: String, expirationTime: DateTime) extends Token {
	def isExpired = expirationTime.isBeforeNow
}

object TokenManager {
	
	val tokens = scala.collection.mutable.HashMap[String, TokenManager]()
	
	def findById (id: String): Future[Option[TokenManager]] = {
		Future.successful(tokens.get(id))
	}
	
	def save (token: TokenManager): Future[TokenManager] = {
		tokens += (token.id -> token)
		Future.successful(token)
	}
	
	def delete (id: String): Unit = {
		tokens.remove(id)
	}
}