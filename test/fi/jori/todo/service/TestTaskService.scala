package fi.jori.todo.service

import org.specs2.mutable.Specification
import fi.jori.todo.model.Task
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.matcher.ResultMatchers
import java.util.Date

@RunWith(classOf[JUnitRunner])
class TestTaskService extends Specification with ResultMatchers {

  def service = new TaskService()
  def task = Task(topic="topic of task",explanation="some long explanation of task")
  
  def createdTask = service.create(task)
  
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
    "have a start date" in {
      createdTask.todo must not be None
    }
  }  
  
  "Created task" should {
    "have a start date before current time" in {
      createdTask.todo.get must be before(new Date)
    }
  }
}