package fi.jori.todo.service

import org.specs2.mutable.Specification
import fi.jori.todo.model.Task
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.matcher.ResultMatchers
import org.specs2.matcher.Matcher
import java.util.Date

@RunWith(classOf[JUnitRunner])
class TestTaskService extends Specification with ResultMatchers {

  lazy val service = new TaskService()
  lazy val createdTask = service.create("topic of task", "some long explanation of task")
  
  "Created task" should {
    "have uuid" in {
      createdTask.id must beSome
    }
    "have uuid length greater that zero" in {
      createdTask.id must beSome((id: String) => id must not be empty)
    }
    "have a creation date" in {
      createdTask.created must beSome
    }
    "have a creation date before current time" in {
      createdTask.created must beBeforeNow
    }
  }
    
  lazy val updatedNotStartedTask = service.update(createdTask.id.get, "New topic", "improved explanation")
  
  "Updated task" should {
    "not have a start time" in {
      updatedNotStartedTask.started must beNone
    } 
    "have a new topic" in {
      updatedNotStartedTask.topic must not be equalTo(createdTask.topic)
    } 
    "have a new explanation" in {
      updatedNotStartedTask.explanation must not be equalTo(createdTask.explanation)
    } 
  }
  
  lazy val startedTask = service.start(createdTask.id.get)
  
  "Started task" should {
    "have same uuid as original" in {
      startedTask.id must beSome((id: String) => createdTask.id must beSome(===(id)))
    }
    "have a start date" in {
      startedTask.started must beSome
    }
    "have a start time after creation time" in {
      startedTask.created must beAfter(createdTask.created)
    }
  }
  
  lazy val restartTask = service.start(startedTask.id.get)
  "Starting task again" should {
    "not update the start time" in {
      restartTask.started must haveTheSameDateAs(startedTask.started)
    }
  }
  
  "Starting non existing task" should {
    "throw exception" in {
      service.start("123") must throwA[NoSuchElementException]
    }
  }

  lazy val finishedTask = service.finish(createdTask.id.get)
  
  "Finished task" should {
    "have a finish time" in {
      finishedTask.finished must beSome
    }

    "have a finish time after creation time" in {
      finishedTask.finished must beAfter(createdTask.created)
    }

    "have a finish time after start time" in {
      finishedTask.finished must beAfter(finishedTask.started)
    }

    "stay finished" in {
      service.start(finishedTask.id.get) must throwA[Exception]
    }
  }
  
  "Getting existing task" should {
    "return a valid task" in {
      // find should return an Option!
      service.find(finishedTask.id.get) must not beNull
    }
  }
  
  "Getting non existing task" should {
    "return error instead of task" in {
      service.find("123") must throwA[NoSuchElementException]
    }
  }
  
  lazy val createdOther = service.create("topic of other task", "some long explanation of other task")
  lazy val finishOther = service.finish(createdOther.id.get)
  
  "Finishing task that's not started" should {
    "add a start time" in {
      finishOther.started must beSome
    }

    "have start time before finish time" in {
      finishOther.started must beBefore(finishOther.finished)
    }
  }
    
  lazy val removedTask = service.remove(finishedTask.id.get)
  "Deleting task" should {
    "mark it removed" in {
      removedTask.deleted must beSome
    }
  }

  def beAfter(date: Option[Date]): Matcher[Option[Date]] = (actual: Option[Date]) =>
    actual must beSome((d: Date) => date must beSome(be_<=(d)))

  def beBefore(date: Option[Date]): Matcher[Option[Date]] = (actual: Option[Date]) =>
    actual must beSome((d: Date) => date must beSome(be_>=(d)))

  def beBeforeNow: Matcher[Option[Date]] = (actual: Option[Date]) =>
    (actual must beSome((d: Date) => d.getTime must be_<=((new Date).getTime))).toResult

  def haveTheSameDateAs(date: Option[Date]): Matcher[Option[Date]] = (actual: Option[Date]) =>
    actual must beSome((d: Date) => date must beSome(be_==(d)))
}