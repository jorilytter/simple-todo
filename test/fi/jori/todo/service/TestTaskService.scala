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
  val createdTask = service.create("topic of task", "some long explanation of task")
  
  "Created task" should {
    "have uuid" in {
      createdTask._id must not be None 
    }
    "have uuid length greater that zero" in {
      createdTask._id.get.toString().length() must be > 0
    }
    "have a creation date" in {
      createdTask.created must not be None
    }
    "have a creation date before current time" in {
      createdTask.created.get.getTime() must be <= new Date().getTime()
    }
  }
/*
  val updatedNotStartedTask = service.update(createdTask._id.get.toString(), "New topic", "improved explanation")
  
  "Updated task" should {
    "not have a start time" in {
      updatedNotStartedTask.started must be equalTo(None)
    } 
    "have a new topic" in {
      updatedNotStartedTask.topic must not be equalTo(createdTask.topic)
    } 
    "have a new explanation" in {
      updatedNotStartedTask.explanation must not be equalTo(createdTask.explanation)
    } 
  }

  val startedTask = service.start(createdTask._id.get.toString())
  
  "Started task" should {
    "have same uuid as original" in {
      startedTask._id.get must be equalTo(createdTask._id.get)
    }
    "have a start date" in {
      startedTask.started must not be None
    }
    "have a start time after creation time" in {
      startedTask.created.get.getTime() must be >= createdTask.created.get.getTime()
    }
  }
  
  val restartTask = service.start(startedTask._id.get.toString())
  "Starting task again" should {
    "not update the start time" in {
      restartTask.started.get.getTime() must be equalTo(startedTask.started.get.getTime())
    }
  }
  
  "Starting non existing task" should {
    "throw exception" in {
      service.start("123") must throwA[NoSuchElementException]
    }
  }

  val finishedTask = service.finish(createdTask._id.get.toString())
  
  "Finished task" should {
    "have a finish time" in {
      finishedTask.finished must not be None
    }
    "have a finish time after creation time" in {
      finishedTask.finished.get.getTime() must be >= createdTask.created.get.getTime()
    }
    "have a finish time after start time" in {
      finishedTask.finished.get.getTime() must be >= finishedTask.started.get.getTime()
    }
    "stay finished" in {
      service.start(finishedTask._id.get.toString()) must throwA[Exception]
    }
  }
  
  "Getting existing task" should {
    "return a valid task" in {
      service.find(finishedTask._id.get.toString()) must not be None
    }
  }
  
  "Getting non existing task" should {
    "return error instead of task" in {
      service.find("123") must throwA[NoSuchElementException]
    }
  }
  
  val createdOther = service.create("topic of other task", "some long explanation of other task")
  val finishOther = service.finish(createdOther._id.get.toString())
  
  "Finishing task that's not started" should {
    "add a start time" in {
      finishOther.started.get must not be None
    }
    "have start time before finish time" in {
      finishOther.started.get.getTime() must be <= finishOther.finished.get.getTime()
    }
  }
  
  val removedTask = service.remove(finishedTask._id.get.toString())
  "Deleting task" should {
    "mark it removed" in {
      removedTask.deleted must not be None
    }
  }
  */
}