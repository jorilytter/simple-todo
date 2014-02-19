package fi.jori.todo.service

import java.util.Date
import org.junit.runner.RunWith
import org.specs2.execute.AsResult
import org.specs2.execute.Result
import org.specs2.matcher.ResultMatchers
import org.specs2.mutable.BeforeAfter
import org.specs2.mutable.Specification
import com.mongodb.casbah.commons.MongoDBObject
import fi.jori.todo.model.Task
import play.api.test.WithApplication
import org.specs2.runner.JUnitRunner
import org.specs2.mutable.After
import play.api.Play

abstract class WithTaskService extends WithApplication {
  
  val service: TaskService = new TaskService()
  def createdTask = service.create("topic of task", "some long explanation of task")
  def update(id: String) = service.update((id: String), "New topic", "improved explanation")
  def updatedNotStartedTask(id: String) = service.update(id, "New topic", "improved explanation")
  
  override def around[T: AsResult](t: => T): Result = super.around {
    val remove = Task.remove(MongoDBObject.empty)
    t
  }
}

@RunWith(classOf[JUnitRunner])
class TestTaskService extends Specification with ResultMatchers {

  "Created task" should {
    "have uuid" in new WithTaskService {
      val task = createdTask
      task._id must not be None
    }
    "have uuid length greater that zero" in new WithTaskService {
      val task = createdTask
      task._id.get.toString().length() must be > 0
    }
    "have a creation date" in new WithTaskService {
      val task = createdTask
      task.created must not be None
    }
    "have a creation date before current time" in new WithTaskService {
      val task = createdTask
      task.created.get.getTime() must be <= new Date().getTime()
    }
  }

  "Updated task" should {
    "not have a start time" in new WithTaskService {
      val created = createdTask
      val task = updatedNotStartedTask(created._id.get.toString())
      task.started must be equalTo(None)
    } 
    "have a new topic" in new WithTaskService {
      val created = createdTask
      val task = updatedNotStartedTask(created._id.get.toString())
      task.topic must not be equalTo(created.topic)
    } 
    "have a new explanation" in new WithTaskService {
      val created = createdTask
      val task = updatedNotStartedTask(created._id.get.toString())
      task.explanation must not be equalTo(created.explanation)
    } 
  }

  "Started task" should {
    "have same uuid as original" in new WithTaskService {
      val created = createdTask
      val task = service.start(created._id.get.toString())
      task._id.get must be equalTo(created._id.get)
    }
    "have a start date" in new WithTaskService {
      val created = createdTask
      val task = service.start(created._id.get.toString())
      task.started must not be None
    }
    "have a start time after creation time" in new WithTaskService {
      val created = createdTask
      val task = service.start(created._id.get.toString())
      task.created.get.getTime() must be >= created.created.get.getTime()
    }
  }
  
  "Starting task again" should {
    "not update the start time" in new WithTaskService {
      val created = createdTask
      val started = service.start(created._id.get.toString())
      val task = service.start(created._id.get.toString())
      task.started.get.getTime() must be equalTo(started.started.get.getTime())
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
    }
    "have a finish time after creation time" in new WithTaskService {
      val created = createdTask
      val task = service.finish(created._id.get.toString())
      task.finished.get.getTime() must be >= created.created.get.getTime()
    }
    "have a finish time after start time" in new WithTaskService {
      val created = createdTask
      val started = service.start(created._id.get.toString())
      val task = service.finish(created._id.get.toString())
      task.finished.get.getTime() must be >= task.started.get.getTime()
    }
    "stay finished" in new WithTaskService {
      val created = createdTask
      val task = service.finish(created._id.get.toString())
      service.start(task._id.get.toString()) must throwA[Exception]
    }
  }
  
  "Getting existing task" should {
    "return a valid task" in new WithTaskService {
      val created = createdTask
      service.find(created._id.get.toString()) must not be None
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
    }
    "have start time before finish time" in new WithTaskService {
      val created = createdTask
      val task = service.finish(created._id.get.toString())
      task.started.get.getTime() must be <= task.finished.get.getTime()
    }
  }
  
  "Deleting task" should {
    "mark it removed" in new WithTaskService {
      val created = createdTask
      val task = service.remove(created._id.get.toString())
      task.removed must not be None
    }
  }
  
  "Finding all removed tasks" should {
    "find two tasks" in new WithTaskService {
      val task1 = service.create("topic 1", "exp 1")
      val task2 = service.create("topic 2", "exp 2")
      val removed1 = service.remove(task1._id.get.toString())
      val removed2 = service.remove(task2._id.get.toString())
      service.findRemoved.size must be equalTo(2)
    }
  }
  
  "Finding all finished tasks" should {
    "find two tasks" in new WithTaskService {
      val task1 = service.create("topic 1", "exp 1")
      val task2 = service.create("topic 2", "exp 2")
      val finished1 = service.finish(task1._id.get.toString())
      val finished2 = service.finish(task2._id.get.toString())
      service.findFinished.size must be equalTo(2)
    }
    "find two tasks and no removed tasks" in new WithTaskService {
      val task1 = service.create("topic 1", "exp 1")
      val task2 = service.create("topic 2", "exp 2")
      val task3 = service.create("topic 3", "exp 3")
      val finished1 = service.finish(task1._id.get.toString())
      val finished2 = service.finish(task2._id.get.toString())
      val removed3 = service.remove(task3._id.get.toString())
      service.findFinished.size must be equalTo(2)
    }
  }
  
  "Finding all started tasks" should {
    "find one started task" in new WithTaskService {
      val task1 = service.create("topic 1", "exp 1")
      val task2 = service.create("topic 2", "exp 2")
      val task3 = service.create("topic 3", "exp 3")
      val started = service.start(task1._id.get.toString())
      val finished = service.finish(task2._id.get.toString())
      val removed = service.remove(task3._id.get.toString())
      service.findStarted.size must be equalTo(1)
    }
  }
  
  "Finding all created tasks" should {
    "find tasks with only created timestamp" in new WithTaskService {
      val task1 = service.create("topic 1", "exp 1")
      val task2 = service.create("topic 2", "exp 2")
      val task3 = service.create("topic 3", "exp 3")
      val started = service.start(task1._id.get.toString())
      val finished = service.finish(task2._id.get.toString())
      service.findCreated.size must be equalTo(1)
    }
  }
}