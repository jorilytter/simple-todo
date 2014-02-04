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
      createdTask.created must not be None
    }
  }  
  
  "Created task" should {
    "have a creation date before current time" in {
      createdTask.created.get must be before(new Date)
    }
  }
  
  def updatedNotStartedTask = service.update(createdTask.id.get, "New topic", "improved explanation")
  
  "Updated task" should {
    "not have a start time" in {
      updatedNotStartedTask.started === None
    } 
  }
  
  "Updated task" should {
    "have a new topic" in {
      updatedNotStartedTask.topic must not be equalTo(createdTask.topic)
    } 
  }
  
  "Updated task" should {
    "have a new explanation" in {
      updatedNotStartedTask.explanation must not be equalTo(createdTask.explanation)
    } 
  }
  
  def startedTask = service.start(createdTask.id.get)
  
  "Started task" should {
    "have same uuid as original" in {
      startedTask.id.get must be equalTo(createdTask.id.get)
    }
  }
  
  "Started task" should {
    "have a start date" in {
      startedTask.started must not be None
    }
  }  
  
  "Started task" should {
    "have a start time after creation time" in {
      startedTask.created.get must be after(createdTask.created.get)
    }
  }
}