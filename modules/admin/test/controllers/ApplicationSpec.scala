package controllers

import models.Manager
import utils.silhouette._
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import com.mohiva.play.silhouette.api.{ Environment, LoginInfo }
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.test._
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.concurrent.Execution.Implicits._
import play.api.test.{ FakeRequest, PlaySpecification, WithApplication }
import java.io.File
import controllers.admin.routes

/**
 * Test case for the [[controllers.Application]] class.
 */
class ApplicationSpec extends PlaySpecification with Mockito {
  sequential

  "The `index` action" should {
    "redirect to login page if manager is unauthorized" in new Context {
      new WithApplication(application) {
        val Some(redirectResult) = route(app, FakeRequest(routes.Application.myAccount)
          .withAuthenticator[MyEnv[Manager]](LoginInfo("invalid", "invalid")))

        status(redirectResult) must be equalTo SEE_OTHER

        val redirectURL = redirectLocation(redirectResult).getOrElse("")
        redirectURL must contain(routes.Auth.signIn.toString)

        val Some(unauthorizedResult) = route(app, FakeRequest(GET, redirectURL))

        status(unauthorizedResult) must be equalTo OK
        contentType(unauthorizedResult) must beSome("text/html")
        contentAsString(unauthorizedResult) must contain("Sign In")
      }
    }

    "return 200 if manager is authorized" in new Context {
      new WithApplication(application) {
        val Some(result) = route(app, FakeRequest(routes.Application.myAccount)
          .withAuthenticator[MyEnv[Manager]](identity.loginInfo))

        status(result) must beEqualTo(OK)
      }
    }
  }

  /**
   * The context.
   */
  trait Context extends Scope {

    class FakeModule extends AbstractModule with ScalaModule {
      def configure() = {
        bind[Environment[MyEnv[Manager]]].toInstance(env)
      }
    }

    val identity = Manager.managers.head._2

    implicit val env: Environment[MyEnv[Manager]] = new FakeEnvironment[MyEnv[Manager]](Seq(identity.loginInfo -> identity))

    lazy val application = new GuiceApplicationBuilder().in(new File("./modules/admin/")).overrides(new FakeModule()).build
  }
}
