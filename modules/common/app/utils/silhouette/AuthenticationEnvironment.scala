package utils.silhouette

import com.mohiva.play.silhouette.api.{ Identity, Environment, EventBus, SilhouetteEvent }
import com.mohiva.play.silhouette.api.util.{ PasswordInfo, Clock }
import com.mohiva.play.silhouette.impl.authenticators.{ CookieAuthenticator, CookieAuthenticatorService, CookieAuthenticatorSettings }
import com.mohiva.play.silhouette.impl.providers.{ CredentialsProvider }
import com.mohiva.play.silhouette.impl.repositories.{ DelegableAuthInfoRepository }
import com.mohiva.play.silhouette.impl.daos.{ DelegableAuthInfoDAO }
import com.mohiva.play.silhouette.impl.util.{ DefaultFingerprintGenerator, SecureRandomIDGenerator, BCryptPasswordHasher }
import play.api.Configuration
import scala.concurrent.duration._

trait AuthenticationEnvironment[I <: Identity, T <: MailToken] extends Environment[I, CookieAuthenticator] with utils.ConfigSupport {

  val conf: Configuration
  val passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo]
  val tokenService: MailTokenService[T]

  override implicit val executionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext
  val clock = Clock()
  val eventBus = EventBus()
  def publish(event: SilhouetteEvent) = eventBus.publish(event)

  val requestProviders = Seq()
  val passwordHasher = new BCryptPasswordHasher()
  def authInfo(password: String): PasswordInfo = passwordHasher.hash(password)

  val authenticatorService = {
    val cfg = requiredConfig("silhouette.authenticator")
    new CookieAuthenticatorService(
      CookieAuthenticatorSettings(
        cookieName = confRequiredString("cookieName", cfg),
        cookiePath = confRequiredString("cookiePath", cfg),
        secureCookie = confRequiredBoolean("secureCookie", cfg),
        httpOnlyCookie = confRequiredBoolean("httpOnlyCookie", cfg),
        useFingerprinting = confRequiredBoolean("useFingerprinting", cfg),
        cookieMaxAge = confGetInt("cookieMaxAge", cfg).map(_.seconds),
        authenticatorIdleTimeout = confGetInt("authenticatorIdleTimeout", cfg).map(_.seconds),
        authenticatorExpiry = confRequiredInt("authenticatorExpiry", cfg).seconds
      ),
      None,
      fingerprintGenerator = new DefaultFingerprintGenerator(false),
      idGenerator = new SecureRandomIDGenerator(),
      clock
    )
  }

  lazy val authInfoRepository = new DelegableAuthInfoRepository(passwordInfoDAO)
  lazy val credentialsProvider = new CredentialsProvider(authInfoRepository, passwordHasher, Seq(passwordHasher))

  private lazy val rememberMeParams: (Int, Option[FiniteDuration], Option[FiniteDuration]) = {
    val cfg = requiredConfig("silhouette.authenticator.rememberMe")
    (
      confRequiredInt("authenticatorExpiry", cfg),
      Some(confRequiredInt("authenticatorIdleTimeout", cfg).seconds),
      Some(confRequiredInt("cookieMaxAge", cfg).seconds)
    )
  }
  def authenticatorWithRememberMe(authenticator: CookieAuthenticator, rememberMe: Boolean) = {
    if (rememberMe) {
      authenticator.copy(
        expirationDateTime = clock.now plusSeconds rememberMeParams._1,
        idleTimeout = rememberMeParams._2,
        cookieMaxAge = rememberMeParams._3
      )
    } else
      authenticator
  }
}