package fi.jori.todo.model

import java.util.Date
import org.bson.types.ObjectId
import com.mongodb.casbah.MongoConnection
import com.novus.salat.dao.ModelCompanion
import com.novus.salat.dao.SalatDAO
import fi.jori.todo.app.ctx
import com.mongodb.DBObject
import com.mongodb.casbah.Imports._
import play.Configuration
import play.api.Play

object Task extends ModelCompanion[Task, ObjectId] {
  
  val db = Play.current.configuration.getString("mongo.db").get
  val collection = Play.current.configuration.getString("tasks.collection").get
  val tasks = MongoConnection()(db)(collection)
  override val dao = new SalatDAO[Task, ObjectId](collection = tasks) {}
  
  def all = Task.findAll.toStream // FIXME: this might be a bad idea!
  
  def removedTasks: List[Task] = {
    val query: DBObject = "removed" $exists true
    val sort: DBObject = MongoDBObject("removed" -> -1)
    Task.find(query).sort(orderBy = sort).toList
  }
  
  def finishedTasks: List[Task] = {
    val query: DBObject = ("removed" $exists false) ++ ("finished" $exists true)
    val sort: DBObject = MongoDBObject("finished" -> -1)
    Task.find(query).sort(orderBy = sort).toList
  }
  
  def startedTasks: List[Task] = {
    val query: DBObject = ("removed" $exists false) ++ ("finished" $exists false) ++ ("started" $exists true)
    val sort: DBObject = MongoDBObject("started" -> 1)
    Task.find(query).sort(orderBy = sort).toList
  }
  
  def createdTasks: List[Task] = {
    val query: DBObject = ("removed" $exists false) ++ ("finished" $exists false) ++ ("started" $exists false)
    val sort: DBObject = MongoDBObject("created" -> 1)
    Task.find(query).sort(orderBy = sort).toList
  }
  
  def create(task: Task): Task = { 
    val id = Task.insert(task)
    Task.findOneById(id.get).get
  }
  
  def saveTask(task: Task): Task = {
    val result  = Task.save(task)
    Task.findOneById(task._id.get).get
  }
}

case class Task(_id: Option[ObjectId] = None,
    topic: String,
    explanation: String,
    created: Option[Date] = None,
    started: Option[Date] = None,
    finished: Option[Date] = None,
    removed: Option[Date] = None) {}