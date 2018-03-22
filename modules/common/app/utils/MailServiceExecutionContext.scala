package utils

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext
import javax.inject.{ Inject, Singleton }

/**
 * Custom execution context for the mailer service.
 *
 * See https://www.playframework.com/documentation/2.6.x/Migration26#play.api.libs.concurrent.Execution-is-deprecated
 */
@Singleton
class MailServiceExecutionContext @Inject() (actorSystem: ActorSystem)
  extends CustomExecutionContext(actorSystem, "mail_service_dispatcher")
