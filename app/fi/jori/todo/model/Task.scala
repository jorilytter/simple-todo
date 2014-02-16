package fi.jori.todo.model

import java.util.Date
import org.bson.types.ObjectId
import com.mongodb.casbah.MongoConnection
import com.novus.salat.dao.ModelCompanion
import com.novus.salat.dao.SalatDAO
import fi.jori.todo.service.myApp.ctx
import com.novus.salat.annotations.raw.Key

object Task extends ModelCompanion[Task, ObjectId] {
  
  private val tasks = MongoConnection()("simple-todo")("tasks")
  override val dao = new SalatDAO[Task, ObjectId](collection = tasks) {}
  
  def all = Task.findAll.toStream // FIXME: this might be a bad idea!
  
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
    deleted: Option[Date] = None) {}