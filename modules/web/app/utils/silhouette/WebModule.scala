package utils.silhouette

import com.google.inject.{ AbstractModule, Provides }
import net.codingwell.scalaguice.ScalaModule
import com.google.inject.name.Named
import com.mohiva.play.silhouette.api.actions.{ SecuredErrorHandler, UnsecuredErrorHandler }
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.{ IdentityService, AuthenticatorService }
import com.mohiva.play.silhouette.api.util.{ PasswordInfo, PasswordHasherRegistry }
import com.mohiva.play.silhouette.api.{ Environment, EventBus, Silhouette, SilhouetteProvider }
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import models.{ User, MailTokenUser }
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import play.api.Configuration
import play.api.libs.concurrent.Execution.Implicits._

/**
 * The Guice module which wires all Silhouette dependencies.
 */
class WebSilhouetteModule extends AbstractModule with ScalaModule {

  /**
   * Configures the module.
   */
  def configure() {
    bind[Silhouette[MyEnv[User]]].to[SilhouetteProvider[MyEnv[User]]]
    bind[IdentityService[User]].to[UserService]
    bind[MailTokenService[MailTokenUser]].to[MailTokenUserService]
  }

  /**
   * Provides the Silhouette environment.
   *
   * @param userService The user service implementation.
   * @param authenticatorService The authentication service implementation.
   * @param eventBus The event bus instance.
   * @return The Silhouette environment.
   */
  @Provides
  def provideEnvironment(
    userService: UserService,
    authenticatorService: AuthenticatorService[CookieAuthenticator],
    eventBus: EventBus
  ): Environment[MyEnv[User]] = {
    Environment[MyEnv[User]](userService, authenticatorService, Seq(), eventBus)
  }

  /**
   * Provides the delegate auth info dao.
   */
  @Provides @Named("web-pwd-info-dao")
  def providePasswordInfoDAO(): DelegableAuthInfoDAO[PasswordInfo] = {
    new PasswordInfoWebDAO()
  }

  /**
   * Provides the auth info repository.
   *
   * @param passwordInfoDAO The implementation of the delegable password auth info DAO.
   * @return The auth info repository instance.
   */
  @Provides @Named("web-auth-info-repository")
  def provideAuthInfoRepository(
    @Named("web-pwd-info-dao") passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo]
  ): AuthInfoRepository = {
    new DelegableAuthInfoRepository(passwordInfoDAO)
  }

  /**
   * Provides the credentials provider.
   *
   * @param authInfoRepository The auth info repository implementation.
   * @param passwordHasherRegistry The password hasher registry.
   * @return The credentials provider.
   */
  @Provides @Named("web-credentials-provider")
  def provideCredentialsProvider(
    @Named("web-auth-info-repository") authInfoRepository: AuthInfoRepository,
    passwordHasherRegistry: PasswordHasherRegistry
  ): CredentialsProvider = {
    new CredentialsProvider(authInfoRepository, passwordHasherRegistry)
  }
}

/**
 * The Guice module which wires Error Handler for Silhouette.
 */
class WebSilhouetteErrorHandlerModule extends AbstractModule with ScalaModule {
  def configure() {
    bind[SecuredErrorHandler].to[web.ErrorHandler]
    bind[UnsecuredErrorHandler].to[web.ErrorHandler]
  }
}