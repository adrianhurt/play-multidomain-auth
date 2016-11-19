package utils.web

import utils.MailService
import models.User
import play.twirl.api.Html
import play.api.i18n.Messages
import views.html.web.mails
import javax.inject.{ Singleton, Inject }

@Singleton
class Mailer @Inject() (ms: MailService) {

  implicit def html2String(html: Html): String = html.toString

  def welcome(user: User, link: String)(implicit m: Messages) {
    ms.sendEmailAsync(user.email)(
      subject = Messages("web.mail.welcome.subject"),
      bodyHtml = mails.welcome(user.firstName, link),
      bodyText = mails.welcomeTxt(user.firstName, link)
    )
  }

  def forgotPassword(email: String, link: String)(implicit m: Messages) {
    ms.sendEmailAsync(email)(
      subject = Messages("web.mail.forgotpwd.subject"),
      bodyHtml = mails.forgotPassword(email, link),
      bodyText = mails.forgotPasswordTxt(email, link)
    )
  }

}