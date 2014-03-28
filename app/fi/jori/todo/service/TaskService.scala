package fi.jori.todo.service

import fi.jori.todo.model.TaskDAL
import java.sql.Date
import scala.collection.mutable.Map
import scala.slick.jdbc.JdbcBackend.{Database, Session}
import fi.jori.todo.model.TaskDAL
import fi.jori.todo.model.Task
import play.api.Play
import play.Configuration

class TaskService(dbUrl: String, user: String, pass: String, driver: String) {
  
  val db = Database.forURL(dbUrl, user = user, password = pass, driver = driver)
  val tasks = new TaskDAL()
  implicit def session: Session = db.createSession
  
  private def getDate = new Date(System.currentTimeMillis())
  
  private def startTime(started: Option[Date]) = {
      started match { 
        case None => Some(getDate) 
        case _ => started 
      }
    }

  def find(): List[Task] = {
    tasks.find
  }
  
  def find(id: String): Task = {
   	tasks.find(id)
  }
  
  def create(topic: String, explanation: String): Task = {
    val uuid: String = java.util.UUID.randomUUID().toString();
    val newTask = Task(id=Some(uuid), 
        created=Some(getDate), 
        topic=topic, 
        explanation=explanation)
        
    tasks.create(newTask)
    find(uuid)
  }
  
  def start(id: String): Task = {
    tasks.start(id)
    find(id)
  }
  
  def update(id: String, topic: String, explanation: String): Task = {
    tasks.update(id, topic, explanation)
    find(id)
  }
  
  def finish(id: String): Task = {
    tasks.finish(id)
    find(id)
  }
  
  def remove(id: String): Task = {
    tasks.remove(id)
    find(id)
  }
}