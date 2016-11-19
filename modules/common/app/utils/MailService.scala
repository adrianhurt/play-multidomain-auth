package utils

import play.api.Play.current
import akka.actor.ActorSystem
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.mailer._
import play.api.Configuration
import net.ceedubs.ficus.Ficus._
import scala.concurrent.duration._
import javax.inject.{ Singleton, Inject }

@Singleton
class MailService @Inject() (mailerClient: MailerClient, system: ActorSystem, conf: Configuration) {

  lazy val from = conf.underlying.as[String]("play.mailer.from")

  def sendEmailAsync(recipients: String*)(subject: String, bodyHtml: String, bodyText: String) = {
    system.scheduler.scheduleOnce(100 milliseconds) {
      sendEmail(recipients: _*)(subject, bodyHtml, bodyText)
    }
  }
  def sendEmail(recipients: String*)(subject: String, bodyHtml: String, bodyText: String) =
    mailerClient.send(Email(subject, from, recipients, Some(bodyText), Some(bodyHtml)))
}