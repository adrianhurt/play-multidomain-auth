package utils.admin

import utils.MailService._
import models._
import play.api._
import play.api.mvc._
import play.twirl.api.Html
import scala.language.implicitConversions
import play.api.i18n.Messages
import views.html.admin.mails

object Mailer {
	
	implicit def html2String (html: Html): String = html.toString
	
	def welcome (recipient: String, name: String) {
		sendEmailAsync(recipient)(
			subject = Messages("mail.welcome.subject"),
			bodyHtml = mails.welcome(name),
			bodyText = mails.welcomeTxt(name)
		)
	}
	
}