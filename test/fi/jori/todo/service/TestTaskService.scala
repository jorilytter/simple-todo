package fi.jori.todo.service

import org.specs2.mutable.Specification
import fi.jori.todo.model.Task
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.matcher.ResultMatchers
import java.util.Date
import org.specs2.specification._
import play.api.test.FakeApplication
import play.api.test.WithApplication
import play.api.test.WithApplication
import org.specs2.execute.AsResult
import org.specs2.execute.Result
import org.bson.types.ObjectId

abstract class WithTaskService extends WithApplication {
  
  val service: TaskService = new TaskService()
  def createdTask = service.create("topic of task", "some long explanation of task")
  def update(id: String) = service.update((id: String), "New topic", "improved explanation")
  def updatedNotStartedTask(id: String) = service.update(id, "New topic", "improved explanation")
  
  override def around[T: AsResult](t: => T): Result = super.around {
    t
  }
}

@RunWith(classOf[JUnitRunner])
class TestTaskService extends Specification with ResultMatchers {

  "Created task" should {
    "have uuid" in new WithTaskService {
      val task = createdTask
      task._id must not be None
      Task.remove(task)
    }
    "have uuid length greater that zero" in new WithTaskService {
      val task = createdTask
      task._id.get.toString().length() must be > 0
      Task.remove(task)
    }
    "have a creation date" in new WithTaskService {
      val task = createdTask
      task.created must not be None
      Task.remove(task)
    }
    "have a creation date before current time" in new WithTaskService {
      val task = createdTask
      task.created.get.getTime() must be <= new Date().getTime()
      Task.remove(task)
    }
  }

  "Updated task" should {
    "not have a start time" in new WithTaskService {
      val created = createdTask
      val task = updatedNotStartedTask(created._id.get.toString())
      task.started must be equalTo(None)
      Task.remove(task)
    } 
    "have a new topic" in new WithTaskService {
      val created = createdTask
      val task = updatedNotStartedTask(created._id.get.toString())
      task.topic must not be equalTo(created.topic)
      Task.remove(task)
    } 
    "have a new explanation" in new WithTaskService {
      val created = createdTask
      val task = updatedNotStartedTask(created._id.get.toString())
      task.explanation must not be equalTo(created.explanation)
      Task.remove(task)
    } 
  }

  "Started task" should {
    "have same uuid as original" in new WithTaskService {
      val created = createdTask
      val task = service.start(created._id.get.toString())
      task._id.get must be equalTo(created._id.get)
      Task.remove(task)
    }
    "have a start date" in new WithTaskService {
      val created = createdTask
      val task = service.start(created._id.get.toString())
      task.started must not be None
      Task.remove(task)
    }
    "have a start time after creation time" in new WithTaskService {
      val created = createdTask
      val task = service.start(created._id.get.toString())
      task.created.get.getTime() must be >= created.created.get.getTime()
      Task.remove(task)
    }
  }
  
  "Starting task again" should {
    "not update the start time" in new WithTaskService {
      val created = createdTask
      val started = service.start(created._id.get.toString())
      val task = service.start(created._id.get.toString())
      task.started.get.getTime() must be equalTo(started.started.get.getTime())
      Task.remove(task)
    }
  }
  
  "Starting non existing task" should {
    "throw exception" in new WithTaskService {
      service.start("123") must throwA[Exception]
    }
  }

  "Finished task" should {
    "have a finish time" in new WithTaskService {
      val created = createdTask
      val task = service.finish(created._id.get.toString())
      task.finished must not be None
      Task.remove(task)
    }
    "have a finish time after creation time" in new WithTaskService {
      val created = createdTask
      val task = service.finish(created._id.get.toString())
      task.finished.get.getTime() must be >= created.created.get.getTime()
      Task.remove(task)
    }
    "have a finish time after start time" in new WithTaskService {
      val created = createdTask
      val started = service.start(created._id.get.toString())
      val task = service.finish(created._id.get.toString())
      task.finished.get.getTime() must be >= task.started.get.getTime()
      Task.remove(task)
    }
    "stay finished" in new WithTaskService {
      val created = createdTask
      val task = service.finish(created._id.get.toString())
      service.start(task._id.get.toString()) must throwA[Exception]
      Task.remove(task)
    }
  }
  
  "Getting existing task" should {
    "return a valid task" in new WithTaskService {
      val created = createdTask
      service.find(created._id.get.toString()) must not be None
      Task.remove(created)
    }
  }
  
  "Getting non existing task" should {
    "return error instead of task" in new WithTaskService {
      service.find("123") must throwA[Exception]
    }
  }

  "Finishing task that's not started" should {
    "add a start time" in new WithTaskService {
      val created = createdTask
      val task = service.finish(created._id.get.toString())
      task.started.get must not be None
      Task.remove(task)
    }
    "have start time before finish time" in new WithTaskService {
      val created = createdTask
      val task = service.finish(created._id.get.toString())
      task.started.get.getTime() must be <= task.finished.get.getTime()
      Task.remove(task)
    }
  }
  
  "Deleting task" should {
    "mark it removed" in new WithTaskService {
      val created = createdTask
      val task = service.remove(created._id.get.toString())
      task.deleted must not be None
      Task.remove(task)
    }
  }
}