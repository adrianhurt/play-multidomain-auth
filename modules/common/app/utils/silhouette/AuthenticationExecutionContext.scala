package utils.silhouette

import javax.inject.{ Inject, Singleton }

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext

/**
 * Custom execution context for the Silhouette authentication service, i.e. the secure random generator.
 *
 * See https://www.playframework.com/documentation/2.6.x/Migration26#play.api.libs.concurrent.Execution-is-deprecated
 */
@Singleton
class AuthenticationExecutionContext @Inject() (actorSystem: ActorSystem)
  extends CustomExecutionContext(actorSystem, "authentication_service_dispatcher")
