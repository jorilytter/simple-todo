package fi.jori.todo.service

import java.util.Date

import org.bson.types.ObjectId

import com.novus.salat.Context

import fi.jori.todo.model.Task
import play.api.Play
import play.api.Play.current

class TaskService {
  
  private def startTime(started: Option[Date]) = {
      started match { 
        case None => Some(new Date) 
        case _ => started 
      }
    }

  def find(id: String): Task = {
    Task.findOneById(new ObjectId(id)).get
  }
  
  def create(topic: String, explanation: String): Task = {
    
    val newTask = Task(created=Some(new Date), 
        topic=topic, 
        explanation=explanation)
    
    Task.create(newTask)
  }
  
  def start(id: String): Task = {
    val existingTask = find(id)
    require(existingTask.finished == None, "Task is already finished")
    
    val updateTask = Task(_id=existingTask._id, 
        created=existingTask.created, 
        topic=existingTask.topic, 
        explanation=existingTask.explanation,
        started=startTime(existingTask.started))
    
    Task.saveTask(updateTask)
  }
  
  def update(id: String, topic: String, explanation: String): Task = {
    val existingTask = find(id)
    val updateTask = Task(_id=existingTask._id, 
        created=existingTask.created, 
        topic=topic, 
        explanation=explanation,
        started=existingTask.started,
        finished=existingTask.finished)
      
    Task.saveTask(updateTask)
  }
  
  def finish(id: String): Task = {
    val existingTask = find(id)
    val finishTask = Task(_id=existingTask._id, 
        created=existingTask.created, 
        topic=existingTask.topic, 
        explanation=existingTask.explanation,
        started=startTime(existingTask.started),
        finished=Some(new Date))
        
      Task.saveTask(finishTask)
  }
  
  def remove(id: String): Task = {
    val existingTask = find(id)
    val removeTask = Task(_id=existingTask._id, 
        created=existingTask.created, 
        topic=existingTask.topic, 
        explanation=existingTask.explanation,
        started=existingTask.started,
        finished=existingTask.finished,
        removed=Some(new Date))

   Task.saveTask(removeTask)
  }
  
  def findRemoved: List[Task] = {
    Task.removedTasks
  }
  
  def findFinished: List[Task] = {
    Task.finishedTasks
  }
  
  def findStarted: List[Task] = {
    Task.startedTasks
  }
  
  def findCreated: List[Task] = {
    Task.createdTasks
  }
}

package object myApp {
  implicit val ctx = {
    val c = new Context() {
      val name = "Custom Context"
    }
    c.registerClassLoader(Play.classloader)
    c
  }
}