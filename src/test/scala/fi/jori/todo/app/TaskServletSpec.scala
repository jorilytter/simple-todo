package fi.jori.todo.app

import org.scalatra.test.specs2.MutableScalatraSpec
import org.scalatra.test.specs2.ScalatraSpec
import fi.jori.todo.model.Task

// For more on Specs2, see http://etorreborre.github.com/specs2/guide/org.specs2.guide.QuickStart.html
class TaskServletSpec extends ScalatraSpec { def is =
  "GET / on TaskServlet"                     ^
    "should return status 200"                  ! root200^
                                                end

  addServlet(classOf[TaskServlet], "/*")

  def root200 = get("/") {
    status must_== 200
  }
}

class TaskServletSpecIntegration extends MutableScalatraSpec {
  import org.json4s._
  import org.json4s.jackson.Serialization
  import org.json4s.jackson.JsonMethods._
  import org.json4s.jackson.Serialization.{write => swrite}
  
  implicit val formats = DefaultFormats
  
  addServlet(classOf[TaskServlet], "/*")
  
  val headers = Map("Accept" -> "application/json", "Content-Type" -> "application/json")
  val newTask = swrite(new Task(topic = "topic", explanation = "description"))
  
  "POST "+newTask+" to /task on servlet" should {
    "return status 200 and have id" in {
      post("/task", newTask, headers) {
        status must be equalTo 200
        val createdTask = parse(response.body).extract[Task]
        createdTask.id must not be equalTo(None)
      }
    }
  }
  "GET task at /task/<id>" should {
    "return a task" in {
      post("/task", newTask, headers) {
        status must be equalTo 200
        val createdTask = parse(response.body).extract[Task]
        
        get("/task/"+createdTask.id.get) {
          status must be equalTo 200
          val getTask = parse(response.body).extract[Task]
          getTask must not be equalTo (None)
          getTask.id must not be equalTo (None)
        }
      }
    }
  }
  "GET todo tasks at /task/todo" should {
    "return status 200 and a list of tasks to todo" in {
      post("/task", newTask, headers) {
        status must be equalTo 200

        get("/task/todo") {
          status must be equalTo 200
          val todoTasks = parse(response.body).extract[List[Task]]
          todoTasks.size must be greaterThan 0
        }
      }
    }
  }
  "GET todo tasks at /task/ongoing" should {
    "return status 200 and a list of ongoing tasks" in {
      post("/task", newTask, headers) {
        status must be equalTo (200)
        val task = parse(response.body).extract[Task]
        
        put("/task/"+task.id.get+"/start") {
          status must be equalTo 200
          val startedTask = parse(response.body).extract[Task]
          startedTask.started must not be equalTo (None)

          get("/task/todo") {
            status must be equalTo 200
            val todoTasks = parse(response.body).extract[List[Task]]
            todoTasks.size must be greaterThan 0
          }
        }
      }
    }
  }
  "GET todo tasks at /task/done" should {
    "return status 200 and a list of done tasks" in {
      post("/task", newTask, headers) {
        status must be equalTo (200)
        val task = parse(response.body).extract[Task]
        
        put("/task/"+task.id.get+"/finish") {
          status must be equalTo 200
          val finishedTask = parse(response.body).extract[Task]
          finishedTask.finished must not be equalTo (None)
          finishedTask.started must not be equalTo (None)
          
          get("/task/done") {
            status must be equalTo (200)
            val todoTasks = parse(response.body).extract[List[Task]]
            todoTasks.size must be greaterThan 0
          }
        }
      }
    }
  }
  "GET todo tasks at /task/removed" should {
    "return status 200 and a list of removed tasks" in {
      post("/task", newTask, headers) {
        status must be equalTo (200)
        val task = parse(response.body).extract[Task]
        
        put("/task/"+task.id.get+"/remove") {
          status must be equalTo 200
          val removedTask = parse(response.body).extract[Task]
          removedTask.deleted must not be equalTo (None)
          
          get("/task/removed") {
            status must be equalTo 200
            val todoTasks = parse(response.body).extract[List[Task]]
            todoTasks.size must be greaterThan 0
          }
        }
      }
    }
  }
}
