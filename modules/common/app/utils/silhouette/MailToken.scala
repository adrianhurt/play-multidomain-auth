package utils.silhouette

import org.joda.time.DateTime

trait MailToken {
  def id: String
  def email: String
  def expirationTime: DateTime
  def isExpired = expirationTime.isBeforeNow
}