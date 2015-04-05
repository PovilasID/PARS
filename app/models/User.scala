package models

import java.util.UUID

import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }
import play.api.libs.json.Json
import play.api.libs.json._
import reactivemongo.bson.{BSONDocument, BSONDocumentWriter, BSONObjectID}
import play.modules.reactivemongo.json.BSONFormats._


/**
 * The user object.
 *
 * @param userID The unique ID of the user.
 * @param loginInfo The linked login info.
 * @param firstName Maybe the first name of the authenticated user.
 * @param lastName Maybe the last name of the authenticated user.
 * @param fullName Maybe the full name of the authenticated user.
 * @param email Maybe the email of the authenticated provider.
 * @param avatarURL Maybe the avatar URL of the authenticated provider.
 */
case class User(
  userID: Option[BSONObjectID],
  loginInfo: LoginInfo,
  firstName: Option[String],
  lastName: Option[String],
  fullName: Option[String],
  email: Option[String],
  avatarURL: Option[String]) extends Identity

/**
 * The companion object.
 */
object User extends DataAccess[User]{
  import play.api.libs.json.Json
  import play.modules.reactivemongo.json.BSONFormats._

  def collectionName = "users"

  implicit val loginInfoJsonFormat = Json.format[LoginInfo]
    /**
   * Converts the [User] object to Json and vice versa.
   */
  implicit val jsonFormat:Format[User] = Json.format[User]

  }