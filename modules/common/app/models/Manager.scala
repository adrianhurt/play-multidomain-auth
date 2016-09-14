package models

import utils.silhouette.IdentitySilhouette
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class Manager(
    id: Option[Long],
    email: String,
    password: String,
    firstName: String,
    lastName: String,
    /*
	A manager can have one or more roles. Each role declares a level or area. The 'master' role has full access.
	Ex: ("master") -> full access to every point of the Admin Webpage.
	Ex: ("social") -> the manager works for the 'social' area.
	Ex: ("high", "sales") -> the manager has a 'high' access and works for the 'sales' area.
	*/
    roles: Seq[String]
) extends IdentitySilhouette {
  def key = email
  def fullName: String = firstName + " " + lastName
}

object Manager {

  val managers = scala.collection.mutable.HashMap[Long, Manager](
    1L -> Manager(Some(1L), "master@myweb.com", (new BCryptPasswordHasher()).hash("123123").password, "Eddard", "Stark", Seq("master")),
    2L -> Manager(Some(2L), "social@myweb.com", (new BCryptPasswordHasher()).hash("123123").password, "Margaery", "Tyrell", Seq("social")),
    3L -> Manager(Some(3L), "sales@myweb.com", (new BCryptPasswordHasher()).hash("123123").password, "Petyr", "Baelish", Seq("sales")),
    4L -> Manager(Some(4L), "sales_high@myweb.com", (new BCryptPasswordHasher()).hash("123123").password, "Tyrion", "Lannister", Seq("sales", "high"))
  )

  def findByEmail(email: String): Future[Option[Manager]] = Future.successful(managers.find(_._2.email == email).map(_._2))

  def save(manager: Manager): Future[Manager] = {
    // A rudimentary auto-increment feature...
    def nextId: Long = managers.maxBy(_._1)._1 + 1

    val theManager = if (manager.id.isDefined) manager else manager.copy(id = Some(nextId))
    managers += (theManager.id.get -> theManager)
    Future.successful(theManager)
  }

  def remove(email: String): Future[Unit] = findByEmail(email).map(_.map(u => managers.remove(u.id.get)))
}
