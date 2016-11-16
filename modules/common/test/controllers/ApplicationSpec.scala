package controllers

import utils.silhouette._
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import com.mohiva.play.silhouette.api.{ Environment, LoginInfo }
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.api.actions.{ SecuredErrorHandler, UnsecuredErrorHandler, DefaultSecuredErrorHandler, DefaultUnsecuredErrorHandler }
import com.mohiva.play.silhouette.test._
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.concurrent.Execution.Implicits._
import play.api.test.{ FakeRequest, PlaySpecification, WithApplication }
import java.io.File

class ApplicationSpec extends PlaySpecification with Mockito {

  "Common Module" should {

    "send 404 on a bad request" in new Context {
      new WithApplication(application) {
        route(app, FakeRequest(GET, "/boum")) must beSome.which(status(_) == NOT_FOUND)
      }
    }

    "render the status page" in new Context {
      new WithApplication(application) {
        val home = route(app, FakeRequest(GET, "/status")).get

        status(home) must equalTo(OK)
        contentAsString(home) must contain("Everything is great")
      }
    }
  }

  /**
   * The context.
   */
  trait Context extends Scope {

    class FakeModule extends AbstractModule with ScalaModule {
      def configure() = {
        bind[SecuredErrorHandler].to[DefaultSecuredErrorHandler]
        bind[UnsecuredErrorHandler].to[DefaultUnsecuredErrorHandler]
      }
    }

    lazy val application = new GuiceApplicationBuilder().in(new File("./modules/common/")).overrides(new FakeModule()).build
  }
}