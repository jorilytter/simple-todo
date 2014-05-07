package fi.jori.todo.service

import fi.jori.todo.model.Task
import java.util.Date
import scala.collection.mutable.Map

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
  
  def todo: List[Task] = {
    tasks.filter(t => t._2.deleted == None && t._2.started == None).values.toList
  }
  
  def ongoing: List[Task] = {
    tasks.filter(t => t._2.deleted == None && t._2.started == Some && t._2.finished == None).values.toList
  }
  
  def done: List[Task] = {
    tasks.filter(t => t._2.deleted == None && t._2.started == Some && t._2.finished == Some).values.toList
  }
  
  def removed: List[Task] = {
    tasks.filter(t => t._2.deleted == Some).values.toList
  }
  
  def create(topic: String, explanation: String): Task = {
    val uuid: String = java.util.UUID.randomUUID().toString();
    val newTask = Task(id=Some(uuid), 
        created=Some(new Date), 
        topic=topic, 
        explanation=explanation)
        
    tasks += (uuid -> newTask)
    newTask
  }
  
  def start(id: String): Task = {
    val existingTask = tasks(id)
    require(existingTask.finished == None, "Task is already finished")
    
    val updateTask = Task(id=existingTask.id, 
        created=existingTask.created, 
        topic=existingTask.topic, 
        explanation=existingTask.explanation,
        started=startTime(existingTask.started))
    
    tasks(existingTask.id.get) = updateTask
    updateTask
  }
  
  def update(id: String, topic: String, explanation: String): Task = {
    val existingTask = tasks(id)
    val updateTask = Task(id=existingTask.id, 
        created=existingTask.created, 
        topic=topic, 
        explanation=explanation,
        started=existingTask.started,
        finished=existingTask.finished)
      
    tasks(existingTask.id.get) = updateTask
    updateTask
  }
  
  def finish(id: String): Task = {
    val existingTask = tasks(id)
    val finishTask = Task(id=existingTask.id, 
        created=existingTask.created, 
        topic=existingTask.topic, 
        explanation=existingTask.explanation,
        started=startTime(existingTask.started),
        finished=Some(new Date))
        
    tasks(existingTask.id.get) = finishTask
    finishTask
  }
  
  def remove(id: String): Task = {
    val existingTask = tasks(id)
    val removeTask = Task(id=existingTask.id, 
        created=existingTask.created, 
        topic=existingTask.topic, 
        explanation=existingTask.explanation,
        started=existingTask.started,
        finished=existingTask.finished,
        deleted=Some(new Date))
        
   tasks(existingTask.id.get) = removeTask
   removeTask
  }
}