package fi.jori.todo.service

import fi.jori.todo.model.Task
import java.util.Date
import scala.collection.mutable.Map

class TaskService {
  
  var tasks: Map[String,Task] = Map()

  def find(uid: String): Option[Task] = {
    tasks.get(uid)
  }
  
  def create(task: Task): Task = {
    
    val uuid: String = java.util.UUID.randomUUID().toString();
    val newTask = Task(id=Some(uuid), 
        created=Some(new Date), 
        topic=task.topic, 
        explanation=task.explanation)
        
    tasks += (uuid -> newTask)
    newTask
  }
  
  def start(id: String): Task = {
    
    val existingTask = tasks.get(id).get
    val updateTask = Task(id=existingTask.id, 
        created=existingTask.created, 
        topic=existingTask.topic, 
        explanation=existingTask.explanation,
        started=Some(new Date))
    
    tasks(existingTask.id.get) = updateTask
    updateTask
  }
  
  def update(id: String, topic: String, explanation: String): Task = {
    
    val existingTask = tasks.get(id).get
    val updateTask = existingTask.started match {
      case None => {
        Task(id=existingTask.id, 
        created=existingTask.created, 
        topic=topic, 
        explanation=explanation)
      }
      case _ => {
        Task(id=existingTask.id, 
        created=existingTask.created, 
        topic=topic, 
        explanation=explanation,
        started=existingTask.started)
      }
    }
      
    tasks(existingTask.id.get) = updateTask
    updateTask
  }
  
  def finish(id: String): Task = {
    
    def startTime(started: Option[Date]) = {
      started match { 
        case None => Some(new Date) 
        case _ => started 
      }
    }
    
    val existingTask = tasks.get(id).get
    val finishTask = Task(id=existingTask.id, 
        created=existingTask.created, 
        topic=existingTask.topic, 
        explanation=existingTask.explanation,
        started=startTime(existingTask.started),
        finished=Some(new Date))
        
   tasks(existingTask.id.get) = finishTask
   finishTask
  }
}