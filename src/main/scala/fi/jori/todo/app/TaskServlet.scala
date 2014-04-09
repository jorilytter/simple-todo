package fi.jori.todo.app

import org.scalatra.ScalatraServlet
import org.scalatra.MethodOverride
import fi.jori.todo.service.TaskService
import org.json4s.DefaultFormats
import org.json4s.Formats
import org.scalatra.json.JacksonJsonSupport

case class TaskContents(topic: String, description: String)

class TaskServlet extends ScalatraServlet with MethodOverride with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats
  val service = new TaskService()
  
  before() {
    contentType = formats("json")
  }
  
  private def actionById(action: String => Any): Any = action(params("id"))
  
  get("/task/?") {
    service.tasks
  }

  get("/task/:id/?") {
    actionById(service.find)
  }
  
  post("/task/?") {
    val newTask = parsedBody.extract[TaskContents]
    service.create(newTask.topic, newTask.description)
  }
  
  put("/task/:id/?") {
    val newTask = parsedBody.extract[TaskContents]
    service.update({params("id")}, newTask.topic, newTask.description)
  }
  
  put("/task/:id/remove/?") {
    service.remove({params("id")})
  }
  
  put("/task/:id/start/?") {
    service.start({params("id")})
  }
  
  put("/task/:id/finish/?") {
    service.finish({params("id")})
  }
}