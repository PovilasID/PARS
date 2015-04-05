package models

import play.api.libs.json.Format
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONObjectID
import reactivemongo.core.commands.LastError
import scala.concurrent.Future
import play.api.libs.json._
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global



trait DataAccess[M] {
  def collectionName: String

  implicit val jsonFormat: Format[M]

  private def db = ReactiveMongoPlugin.db

  private def collection = db [JSONCollection](collectionName)

  def DAsave(instance: M): (BSONObjectID, Future[LastError]) = {
    val id = BSONObjectID.generate
    (id, collection.insert(Json.toJson(instance)))
  }



}
