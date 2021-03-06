package models.daos

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import models.daos.PasswordInfoDAO._
import play.api.libs.json.{JsString, JsObject, Json}
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.core.commands.LastError
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable
import scala.concurrent.Future
import reactivemongo.api.Cursor


import models.User

/**
 * The DAO to store the password information.
 */
class PasswordInfoDAO extends DelegableAuthInfoDAO[PasswordInfo] {

  private def db = ReactiveMongoPlugin.db

  private def collection = db [JSONCollection]("users")

  /**
   * Saves the password info.
   *
   * @param loginInfo The login info for which the auth info should be saved.
   * @param authInfo The password info to save.
   * @return The saved password info.
   */
  def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = { //@TODO PasswordInfo needs be serializable
    data += (loginInfo -> authInfo)
    collection.update(
      Json.obj("loginInfo" -> Json.toJson(loginInfo)),
      Json.obj("$set" -> Json.obj( "passwordInfo" -> Json.obj(
        "hasher" -> JsString(authInfo.hasher),
        "password" -> JsString(authInfo.password),
        "salt" -> JsString(authInfo.salt.getOrElse(""))
      )))
    )
    Future.successful(authInfo)
  }


  /**
   * Finds the password info which is linked with the specified login info.
   *
   * @param loginInfo The linked login info.
   * @return The retrieved password info or None if no password info could be retrieved for the given login info.
   */
  def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
    val passList = collection
      .find(Json.obj("loginInfo" -> Json.toJson(loginInfo)))
      .one[JsObject]

    val futureAuth: Future[Option[PasswordInfo]] = passList.map { passDetails =>
      Some(PasswordInfo(
        passDetails.get.\("passwordInfo").\("hasher").toString().replace("\"", ""), //@TODO PasswordInfo REALLY needs be serializable
        passDetails.get.\("passwordInfo").\("password").toString().replace("\"", ""),
        Some(passDetails.get.\("passwordInfo").\("salt").toString().replace("\"", ""))
      ))
    }

    futureAuth
  }
}

/**
 * The companion object.
 */
object PasswordInfoDAO {

  /**
   * The data store for the password info.
   */
  var data: mutable.HashMap[LoginInfo, PasswordInfo] = mutable.HashMap()
}
