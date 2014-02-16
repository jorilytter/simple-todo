package fi.jori.todo.service

import org.specs2.mutable.Specification
import fi.jori.todo.model.Task
import java.util.Date
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import play.api.test.WithApplication

@RunWith(classOf[JUnitRunner])
class TestTask extends Specification {
  
  def createTask: Task = {
    val newTask = Task(created=Some(new Date), 
        topic="some topic", 
        explanation=" some explanation")
    Task.create(newTask)
  }
  
  "Created task" should {
    "have uuid" in new WithApplication {
      val t = createTask
      t._id must not be None
      Task.remove(t)
    }
  }

}