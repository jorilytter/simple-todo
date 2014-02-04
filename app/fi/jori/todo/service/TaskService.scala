package fi.jori.todo.service

import fi.jori.todo.model.Task
import java.util.Date
import scala.collection.mutable.Map

class TaskService {
  
  
  var tasks: Map[String,Task] = Map()

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
    val updateTask = Task(id=existingTask.id, 
        created=existingTask.created, 
        topic=topic, 
        explanation=explanation,
        started=existingTask.started)
    
    tasks(existingTask.id.get) = updateTask
    updateTask
  }
  
  def finish(task: Task): Task = {
    
    val existingTask = tasks.get(task.id.toString()).get
    val finishTask = Task(id=existingTask.id, 
        created=existingTask.created, 
        topic=task.topic, 
        explanation=task.explanation,
        started=existingTask.started,
        finished=Some(new Date))
        
   tasks(finishTask.id.get) = finishTask
   finishTask
  }
}