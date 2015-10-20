package utils.admin

import utils.MailService
import models.User
import play.twirl.api.Html
import play.api.i18n.Messages
import views.html.admin.mails

object Mailer {

  implicit def html2String(html: Html): String = html.toString

  def forgotPassword(email: String, link: String)(implicit ms: MailService, m: Messages) {
    ms.sendEmailAsync(email)(
      subject = Messages("admin.mail.forgotpwd.subject"),
      bodyHtml = mails.forgotPassword(email, link),
      bodyText = mails.forgotPasswordTxt(email, link)
    )
  }

}