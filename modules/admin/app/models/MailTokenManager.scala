package models

import utils.silhouette.MailToken
import org.joda.time.DateTime
import java.util.UUID
import scala.concurrent.Future

case class MailTokenManager(id: String, email: String, expirationTime: DateTime) extends MailToken

object MailTokenManager {
  def apply(email: String): MailTokenManager =
    MailTokenManager(UUID.randomUUID().toString, email, (new DateTime()).plusHours(24))

  val tokens = scala.collection.mutable.HashMap[String, MailTokenManager]()

  def findById(id: String): Future[Option[MailTokenManager]] = {
    Future.successful(tokens.get(id))
  }

  def save(token: MailTokenManager): Future[MailTokenManager] = {
    tokens += (token.id -> token)
    Future.successful(token)
  }

  def delete(id: String): Unit = {
    tokens.remove(id)
  }
}