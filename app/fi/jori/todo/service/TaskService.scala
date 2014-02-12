package fi.jori.todo.service

import fi.jori.todo.model.Task
import java.util.Date
import scala.collection.mutable.Map
import com.mongodb.casbah.MongoConnection
import com.novus.salat._
import com.novus.salat.global._
import com.mongodb.casbah.Imports._
import fi.jori.todo.model.Tasks

class TaskService {
  
  
  var tasks: Map[String,Task] = Map()
  
  private def startTime(started: Option[Date]) = {
      started match { 
        case None => Some(new Date) 
        case _ => started 
      }
    }

  def find(id: String): Task = {
    tasks(id)
  }
  
  def create(topic: String, explanation: String): Task = {
    
    val uuid: String = java.util.UUID.randomUUID().toString();
    val newTask = Task(created=Some(new Date), 
        topic=topic, 
        explanation=explanation)
    
    Tasks.create(newTask)
        
//    val createdTask = grater[Task].asDBObject(newTask)
//    grater[Task].asObject(createdTask)
    
//    val uuid: String = java.util.UUID.randomUUID().toString();
//    val newTask = Task(id=Some(uuid), 
//        created=Some(new Date), 
//        topic=topic, 
//        explanation=explanation)
//        
//    tasks += (uuid -> newTask)
//    newTask
  }
  
  def start(id: String): Task = {
    val existingTask = tasks(id)
    require(existingTask.finished == None, "Task is already finished")
    
    val updateTask = Task(_id=existingTask._id, 
        created=existingTask.created, 
        topic=existingTask.topic, 
        explanation=existingTask.explanation,
        started=startTime(existingTask.started))
    
    tasks(existingTask._id.get.toString()) = updateTask
    updateTask
  }
  
  def update(id: String, topic: String, explanation: String): Task = {
    val existingTask = tasks(id)
    val updateTask = Task(_id=existingTask._id, 
        created=existingTask.created, 
        topic=topic, 
        explanation=explanation,
        started=existingTask.started,
        finished=existingTask.finished)
      
    tasks(existingTask._id.get.toString()) = updateTask
    updateTask
  }
  
  def finish(id: String): Task = {
    val existingTask = tasks(id)
    val finishTask = Task(_id=existingTask._id, 
        created=existingTask.created, 
        topic=existingTask.topic, 
        explanation=existingTask.explanation,
        started=startTime(existingTask.started),
        finished=Some(new Date))
        
    tasks(existingTask._id.get.toString()) = finishTask
    finishTask
  }
  
  def remove(id: String): Task = {
    val existingTask = tasks(id)
    val removeTask = Task(_id=existingTask._id, 
        created=existingTask.created, 
        topic=existingTask.topic, 
        explanation=existingTask.explanation,
        started=existingTask.started,
        finished=existingTask.finished,
        deleted=Some(new Date))
        
   tasks(existingTask._id.get.toString()) = removeTask
   removeTask
  }
}