package utils.silhouette

import com.mohiva.play.silhouette.core.{Silhouette, Identity, Environment, Provider, EventBus, Token}
import com.mohiva.play.silhouette.core.services.{IdentityService, AuthenticatorService, AuthInfoService, TokenService}
import com.mohiva.play.silhouette.core.providers.{PasswordInfo, CredentialsProvider}
import com.mohiva.play.silhouette.core.utils.PasswordHasher
import com.mohiva.play.silhouette.contrib.services.DelegableAuthInfoService
import com.mohiva.play.silhouette.contrib.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.contrib.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.contrib.utils.{PlayCacheLayer, SecureRandomIDGenerator, BCryptPasswordHasher}

trait SilhouetteController[I <: Identity, T <: Token] extends Silhouette[I, CookieAuthenticator] with AuthenticatorServiceModule {

	lazy val eventBus = EventBus()
	lazy val cacheLayer = new PlayCacheLayer
	lazy val idGenerator = new SecureRandomIDGenerator
	lazy val passwordHasher = new BCryptPasswordHasher
	
	lazy val authInfoService = new DelegableAuthInfoService(passwordInfoDAO)
	lazy val credentialsProvider = new CredentialsProvider(authInfoService, passwordHasher, Seq(passwordHasher))
	lazy val providers = Map(credentialsProvider.id -> credentialsProvider)

	def identityService: IdentityService[I]
	def passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo]
	def tokenService: TokenService[T]
	
	def env: Environment[I, CookieAuthenticator]
}