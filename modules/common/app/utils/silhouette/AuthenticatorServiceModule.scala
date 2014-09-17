package utils.silhouette

import play.api.Play
import play.api.Play.current
import com.mohiva.play.silhouette.contrib.authenticators.{CookieAuthenticator, CookieAuthenticatorService, CookieAuthenticatorSettings}
import com.mohiva.play.silhouette.core.utils.{CacheLayer, IDGenerator, Clock}

trait AuthenticatorServiceModule {

	lazy val authenticatorService: CookieAuthenticatorService = new CookieAuthenticatorService(
		CookieAuthenticatorSettings(
			cookieName = Play.configuration.getString("silhouette.authenticator.cookieName").get,
			cookiePath = Play.configuration.getString("silhouette.authenticator.cookiePath").get,
			cookieDomain = Play.configuration.getString("silhouette.authenticator.cookieDomain"),
			secureCookie = Play.configuration.getBoolean("silhouette.authenticator.secureCookie").get,
			httpOnlyCookie = Play.configuration.getBoolean("silhouette.authenticator.httpOnlyCookie").get,
			authenticatorIdleTimeout = Play.configuration.getInt("silhouette.authenticator.authenticatorIdleTimeout").get,
			cookieMaxAge = Play.configuration.getInt("silhouette.authenticator.cookieMaxAge"),
			authenticatorExpiry = Play.configuration.getInt("silhouette.authenticator.authenticatorExpiry").get
		),
		cacheLayer,
		idGenerator,
		Clock()
	)

	def cacheLayer: CacheLayer
	def idGenerator: IDGenerator
}