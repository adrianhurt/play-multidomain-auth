package models

import utils.silhouette.IdentitySilhouette
import com.mohiva.play.silhouette.contrib.utils.BCryptPasswordHasher
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


case class Manager (
	id: Long,
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
	
	val managers = scala.collection.mutable.HashMap[String, Manager](
		"master@myweb.com" -> Manager(1L, "master@myweb.com", (new BCryptPasswordHasher()).hash("123123").password, "Eddard", "Stark", Seq("master")),
		"social@myweb.com" -> Manager(2L, "social@myweb.com", (new BCryptPasswordHasher()).hash("123123").password, "Margaery", "Tyrell", Seq("social")),
		"sales@myweb.com" -> Manager(3L, "sales@myweb.com", (new BCryptPasswordHasher()).hash("123123").password, "Petyr", "Baelish", Seq("sales")),
		"sales_high@myweb.com" -> Manager(4L, "sales_high@myweb.com", (new BCryptPasswordHasher()).hash("123123").password, "Tyrion", "Lannister", Seq("sales", "high"))
	)
	
	def findByEmail (email: String): Future[Option[Manager]] = Future.successful(managers.get(email))
	def findByEmailMap[A] (email: String)(f: Manager => A): Future[Option[A]] = findByEmail(email).map(_.map(f))
	
	def save (manager: Manager): Future[Manager] = {
		managers += (manager.email -> manager)
		Future.successful(manager)
	}
}
