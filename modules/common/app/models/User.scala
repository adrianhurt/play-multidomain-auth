package models

import utils.silhouette.IdentitySilhouette
import com.mohiva.play.silhouette.contrib.utils.BCryptPasswordHasher
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


case class User (
	id: Option[Long],
	email: String,
	emailConfirmed: Boolean,
	password: String,
	nick: String,
	firstName: String,
	lastName: String
) extends IdentitySilhouette {
	def key = email
	def fullName: String = firstName + " " + lastName
}

object User {
	
	val users = scala.collection.mutable.HashMap[String, User](
		"johndoe@myweb.com" -> User(Some(1L), "johndoe@myweb.com", true, (new BCryptPasswordHasher()).hash("123123").password, "johndoe", "John", "Doe")
	)
	
	def findByEmail (email: String): Future[Option[User]] = Future.successful(users.get(email))
	def findByEmailMap[A] (email: String)(f: User => A): Future[Option[A]] = findByEmail(email).map(_.map(f))

	def save (user: User): Future[User] = {
		// A rudimentary auto-increment feature...
		def nextId: Option[Long] = users.maxBy(_._2.id.get)._2.id.map(_ + 1)
		
		val theUser = if (user.id.isDefined) user else user.copy(id = nextId)
		users += (theUser.email -> theUser)
		Future.successful(theUser)
	}
}