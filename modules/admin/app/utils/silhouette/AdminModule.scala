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
import models.{ Manager, MailTokenManager }
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import play.api.Configuration
//import play.api.libs.concurrent.Execution.Implicits._
//import utils.silhouette.AuthenticationExecutionContext

/**
 * The Guice module which wires all Silhouette dependencies.
 */
class AdminSilhouetteModule extends AbstractModule with ScalaModule {

  /**
   * Configures the module.
   */
  def configure() {
    bind[Silhouette[MyEnv[Manager]]].to[SilhouetteProvider[MyEnv[Manager]]]
    bind[IdentityService[Manager]].to[ManagerService]
    bind[MailTokenService[MailTokenManager]].to[MailTokenManagerService]
  }

  /**
   * Provides the Silhouette environment.
   *
   * @param managerService The manager service implementation.
   * @param authenticatorService The authentication service implementation.
   * @param eventBus The event bus instance.
   * @return The Silhouette environment.
   */
  @Provides
  def provideEnvironment(
    managerService: ManagerService,
    authenticatorService: AuthenticatorService[CookieAuthenticator],
    eventBus: EventBus,
    ec: AuthenticationExecutionContext): Environment[MyEnv[Manager]] = {
    Environment[MyEnv[Manager]](managerService, authenticatorService, Seq(), eventBus)(ec)
  }

  /**
   * Provides the delegate auth info dao.
   */
  @Provides @Named("admin-pwd-info-dao")
  def providePasswordInfoDAO(): DelegableAuthInfoDAO[PasswordInfo] = {
    new PasswordInfoAdminDAO()
  }

  /**
   * Provides the auth info repository.
   *
   * @param passwordInfoDAO The implementation of the delegable password auth info DAO.
   * @return The auth info repository instance.
   */
  @Provides @Named("admin-auth-info-repository")
  def provideAuthInfoRepository(
    @Named("admin-pwd-info-dao") passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo],
    ec: AuthenticationExecutionContext): AuthInfoRepository = {
    new DelegableAuthInfoRepository(passwordInfoDAO)(ec)
  }

  /**
   * Provides the credentials provider.
   *
   * @param authInfoRepository The auth info repository implementation.
   * @param passwordHasherRegistry The password hasher registry.
   * @return The credentials provider.
   */
  @Provides @Named("admin-credentials-provider")
  def provideCredentialsProvider(
    @Named("admin-auth-info-repository") authInfoRepository: AuthInfoRepository,
    passwordHasherRegistry: PasswordHasherRegistry,
    ec: AuthenticationExecutionContext): CredentialsProvider = {
    new CredentialsProvider(authInfoRepository, passwordHasherRegistry)(ec)
  }
}

/**
 * The Guice module which wires Error Handler for Silhouette.
 */
class AdminSilhouetteErrorHandlerModule extends AbstractModule with ScalaModule {
  def configure() {
    bind[SecuredErrorHandler].to[admin.ErrorHandler]
    bind[UnsecuredErrorHandler].to[admin.ErrorHandler]
  }
}