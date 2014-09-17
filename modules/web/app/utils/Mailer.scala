package utils.web

import utils.MailService._
import models._
import play.api._
import play.api.mvc._
import play.twirl.api.Html
import scala.language.implicitConversions
import play.api.i18n.Messages
import views.html.web.mails

object Mailer {
	
	implicit def html2String (html: Html): String = html.toString
	
	def welcome (user: User, link: String) {
		sendEmailAsync(user.email)(
			subject = Messages("mail.welcome.subject"),
			bodyHtml = mails.welcome(user.firstName, link),
			bodyText = mails.welcomeTxt(user.firstName, link)
		)
	}
	
	def forgotPassword (email: String, link: String) {
		sendEmailAsync(email)(
			subject = Messages("mail.forgotpwd.subject"),
			bodyHtml = mails.forgotPassword(email, link),
			bodyText = mails.forgotPasswordTxt(email, link)
		)
	}
	
}