package fi.jori.todo.service

import org.specs2.mutable.Specification
import fi.jori.todo.model.Task
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.matcher.ResultMatchers
import java.util.Date

@RunWith(classOf[JUnitRunner])
class TestTaskService extends Specification with ResultMatchers {

  val service = new TaskService()
  val task = Task(topic="topic of task",explanation="some long explanation of task")
  val createdTask = service.create(task)
  
  "Created task" should {
    "have uuid" in {
      createdTask.id must not be None 
    }
  }
    
  "Created task" should {
    "have uuid length greater that zero" in {
      createdTask.id.get.length() must be > 0
    }
  }
  
  "Created task" should {
    "have a creation date" in {
      createdTask.todo must not be None
    }
  }  
  
  "Created task" should {
    "have a creation date before current time" in {
      createdTask.todo.get must be before(new Date)
    }
  }
  
  def updateTask = Task(id=createdTask.id, topic="new topic of task",explanation="some long explanation of task")
  def updatedTask = service.start(updateTask)
  
  "Updated task" should {
    "have same uuid as original" in {
      updatedTask.id.get must be equalTo(createdTask.id.get)
    }
  }
  
  "Updated task" should {
    "have a start date" in {
      updatedTask.ongoing must not be None
    }
  }  
  
  "Updated task" should {
    "have a start time after creation time" in {
      updatedTask.todo.get must be after(createdTask.todo.get)
    }
  }
}