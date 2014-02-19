package fi.jori.todo.model

import java.util.Date
import org.bson.types.ObjectId
import com.mongodb.casbah.MongoConnection
import com.novus.salat.dao.ModelCompanion
import com.novus.salat.dao.SalatDAO
import fi.jori.todo.service.myApp.ctx
import com.mongodb.DBObject
import com.mongodb.casbah.Imports._
import play.Configuration
import play.api.Play

object Task extends ModelCompanion[Task, ObjectId] {
  
  val db = Play.current.configuration.getString("mongo.db").getOrElse("simple-todo")
  val collection = Play.current.configuration.getString("tasks.collection").getOrElse("tasks")
  val tasks = MongoConnection()(db)(collection)
  override val dao = new SalatDAO[Task, ObjectId](collection = tasks) {}
  
  def all = Task.findAll.toStream // FIXME: this might be a bad idea!
  
  def removedTasks: List[Task] = {
    val query: DBObject = "removed" $exists true
    Task.find(query).toList
  }
  
  def finishedTasks: List[Task] = {
    val query: DBObject = ("removed" $exists false) ++ ("finished" $exists true)
    Task.find(query).toList
  }
  
  def startedTasks: List[Task] = {
    val query: DBObject = ("removed" $exists false) ++ ("finished" $exists false) ++ ("started" $exists true)
    Task.find(query).toList
  }
  
  def createdTasks: List[Task] = {
    val query: DBObject = ("removed" $exists false) ++ ("finished" $exists false) ++ ("started" $exists false)
    Task.find(query).toList
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