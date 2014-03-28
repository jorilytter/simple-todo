package fi.jori.todo.service

import java.sql.Date

import scala.slick.jdbc.JdbcBackend.DatabaseDef

import fi.jori.todo.model.Task
import fi.jori.todo.model.TaskDAL

class TaskService(database: DatabaseDef) {
  
  val tasks = new TaskDAL(database)
  
  private def getDate = new Date(System.currentTimeMillis())

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