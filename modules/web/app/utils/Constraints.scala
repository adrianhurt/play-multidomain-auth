package utils

import models.User
import play.api.data.validation._
import play.api.data.validation.Constraints._
import play.api.i18n.Messages
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


object Constraints {
	
	def userExists: Constraint[String] = Constraint("user.exits"){ email =>
		if (userExistsWithEmail(email)) Valid else Invalid(Seq(ValidationError("error.not.exists", "user", "email")))
	}
	def userUnique: Constraint[String] = Constraint("user.unique"){ email =>
		if (!userExistsWithEmail(email)) Valid else Invalid(Seq(ValidationError("error.not.unique", "user", "email")))
	}
	
	private def userExistsWithEmail (email: String): Boolean =
		Await.result(User.findByEmail(email), 100 milliseconds).isDefined

}

