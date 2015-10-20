package utils.silhouette

import scala.concurrent.Future

trait MailTokenService[T <: MailToken] {
  def create(token: T): Future[Option[T]]
  def retrieve(id: String): Future[Option[T]]
  def consume(id: String): Unit
}