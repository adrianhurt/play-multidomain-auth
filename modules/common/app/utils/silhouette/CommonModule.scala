package utils.silhouette

import com.google.inject.{ AbstractModule, Provides }
import net.codingwell.scalaguice.ScalaModule
import com.mohiva.play.silhouette.api.crypto.{ Crypter, CrypterAuthenticatorEncoder, Signer }
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util.{ Clock, FingerprintGenerator, IDGenerator, PasswordHasher, PasswordHasherRegistry }
import com.mohiva.play.silhouette.api.EventBus
import com.mohiva.play.silhouette.crypto.{ JcaCrypter, JcaCrypterSettings, JcaSigner, JcaSignerSettings }
import com.mohiva.play.silhouette.impl.authenticators.{ CookieAuthenticator, CookieAuthenticatorService, CookieAuthenticatorSettings }
import com.mohiva.play.silhouette.impl.util.{ DefaultFingerprintGenerator, SecureRandomIDGenerator }
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import models.User
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import play.api.Configuration
//import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.CookieHeaderEncoding
import utils.MailServiceExecutionContext

/**
 * The Guice module which wires all Silhouette dependencies.
 */
class CommonSilhouetteModule extends AbstractModule with ScalaModule {

  /**
   * Configures the module.
   */
  def configure() {
    // execution contexts
    //bind[MailServiceExecutionContext].toInstance(new MailServiceExecutionContext).asEagerSingleton()
    //bind[AuthenticationExecutionContext].to(classOf[AuthenticationExecutionContext]).asEagerSingleton()

    bind[IDGenerator].to(classOf[SecureRandomIDGenerator]).asEagerSingleton()
    bind[PasswordHasher].toInstance(new BCryptPasswordHasher())
    bind[FingerprintGenerator].toInstance(new DefaultFingerprintGenerator(false))
    bind[EventBus].toInstance(EventBus())
    bind[Clock].toInstance(Clock())
  }

  /**
   * Provides the secure random ID generator using its own execution context.
   * See https://www.playframework.com/documentation/2.6.x/Migration26#play.api.libs.concurrent.Execution-is-deprecated.
   *
   * @param executionContext The execution context
   * @return The secure random ID generator
   */
  @Provides
  def provideSecureRandomIDGenerator(executionContext: AuthenticationExecutionContext): SecureRandomIDGenerator = {
    new SecureRandomIDGenerator()(executionContext)
  }

  /**
   * Provides the cookie signer for the authenticator.
   *
   * @param configuration The Play configuration.
   * @return The cookie signer for the authenticator.
   */
  @Provides
  def provideAuthenticatorSigner(configuration: Configuration): Signer = {
    val config = configuration.underlying.as[JcaSignerSettings]("silhouette.authenticator.cookie.signer")
    new JcaSigner(config)
  }

  /**
   * Provides the crypter for the authenticator.
   *
   * @param configuration The Play configuration.
   * @return The crypter for the authenticator.
   */
  @Provides
  def provideAuthenticatorCrypter(configuration: Configuration): Crypter = {
    val config = configuration.underlying.as[JcaCrypterSettings]("silhouette.authenticator.crypter")
    new JcaCrypter(config)
  }

  /**
   * Provides the authenticator service.
   *
   * @param signer               The cookie signer implementation.
   * @param crypter              The crypter implementation.
   * @param fingerprintGenerator The fingerprint generator implementation.
   * @param idGenerator          The ID generator implementation.
   * @param configuration        The Play configuration.
   * @param clock                The clock instance.
   * @return The authenticator service.
   */
  @Provides
  def provideAuthenticatorService(
    signer: Signer,
    crypter: Crypter,
    cookieHeaderEncoding: CookieHeaderEncoding,
    fingerprintGenerator: FingerprintGenerator,
    idGenerator: IDGenerator,
    configuration: Configuration,
    clock: Clock,
    ec: AuthenticationExecutionContext
  ): AuthenticatorService[CookieAuthenticator] = {
    val config = configuration.underlying.as[CookieAuthenticatorSettings]("silhouette.authenticator")
    val authenticatorEncoder = new CrypterAuthenticatorEncoder(crypter)
    new CookieAuthenticatorService(config, None, signer, cookieHeaderEncoding, authenticatorEncoder, fingerprintGenerator, idGenerator, clock)(ec)
  }

  /**
   * Provides the password hasher registry.
   *
   * @param passwordHasher The default password hasher implementation.
   * @return The password hasher registry.
   */
  @Provides
  def providePasswordHasherRegistry(passwordHasher: PasswordHasher): PasswordHasherRegistry = {
    new PasswordHasherRegistry(passwordHasher)
  }
}