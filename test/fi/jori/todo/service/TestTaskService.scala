package fi.jori.todo.service

import org.specs2.mutable.Specification
import fi.jori.todo.model.Task
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.matcher.ResultMatchers
import java.util.Date

@RunWith(classOf[JUnitRunner])
class TestTaskService extends Specification with ResultMatchers {

  val task = Task(topic="topic of task",explanation="some long explanation of task")
  val service = new TaskService()
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
      createdTask.created.get.getTime() must be <= new Date().getTime()
    }
  }

  val updatedNotStartedTask = service.update(createdTask.id.get, "New topic", "improved explanation")
  
  "Updated task" should {
    "not have a start time" in {
      updatedNotStartedTask.started must be equalTo(None)
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

  val startedTask = service.start(createdTask.id.get)
  
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
      startedTask.created.get.getTime() must be >= createdTask.created.get.getTime()
    }
  }
  
  "Starting non existing task" should {
    "throw exception" in {
      service.start("123") must throwA[NoSuchElementException]
    }
  }

  val finishedTask = service.finish(createdTask.id.get)
  
  "Finished task" should {
    "have a finish time" in {
      finishedTask.finished must not be None
    }
  }
  
  "Finished task" should {
    "have a finish time after creation time" in {
      finishedTask.finished.get.getTime() must be >= createdTask.created.get.getTime()
    }
  }

  "Finished task" should {
    "have a finish time after start time" in {
      finishedTask.finished.get.getTime() must be >= finishedTask.started.get.getTime()
    }
  }
  
  "Getting existing task" should {
    "return a valid task" in {
      service.find(finishedTask.id.get) must not be None
    }
  }
  
  "Getting non existing task" should {
    "return None instead of task" in {
      service.find("123") must be equalTo(None)
    }
  }
  
  val otherTask = Task(topic="topic of other task",explanation="some long explanation of other task")
  val createdOther = service.create(otherTask)
  val finishOther = service.finish(createdOther.id.get)
  
  "Finishing task that's not started" should {
    "add a start time" in {
      finishOther.started.get must not be None
    }
  }
  
  "Finishing task that's not started" should {
    "have start time before finish time" in {
      finishOther.started.get.getTime() must be <= finishOther.finished.get.getTime()
    }
  }
  
  
}