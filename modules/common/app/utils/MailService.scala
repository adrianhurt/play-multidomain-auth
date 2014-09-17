package utils

import models._
import play.api.Play.current
import play.api.libs.concurrent.Akka
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._
import com.typesafe.plugin._

object MailService {
	
	val from = current.configuration.getString("mail.from").get

	def sendEmailAsync (recipients: String*)(subject: String, bodyHtml: String, bodyText: String = "") = {
		Akka.system.scheduler.scheduleOnce(100 milliseconds) {
			sendEmail(recipients: _*)(subject, bodyHtml, bodyText)
		}
	}
	def sendEmail (recipients: String*)(subject: String, bodyHtml: String, bodyText: String = "") = {
		val mail = use[MailerPlugin].email
		mail.setFrom(from)
		mail.setSubject(subject)
		mail.setRecipient(recipients: _*)
		mail.send(bodyText, bodyHtml)
	}
}