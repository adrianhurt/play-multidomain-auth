package models

import com.mohiva.play.silhouette.core.Token
import org.joda.time.DateTime
import java.util.UUID
import scala.concurrent.Future

case class TokenUser (id: String, email: String, expirationTime: DateTime, isSignUp: Boolean) extends Token {
	def isExpired = expirationTime.isBeforeNow
}

object TokenUser {
	def apply (email: String, isSignUp: Boolean): TokenUser =
		TokenUser(UUID.randomUUID().toString, email, (new DateTime()).plusHours(24), isSignUp)
	
	
	val tokens = scala.collection.mutable.HashMap[String, TokenUser]()
	
	def findById (id: String): Future[Option[TokenUser]] = {
		Future.successful(tokens.get(id))
	}
	
	def save (token: TokenUser): Future[TokenUser] = {
		tokens += (token.id -> token)
		Future.successful(token)
	}
	
	def delete (id: String): Unit = {
		tokens.remove(id)
	}
}