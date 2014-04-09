package fi.jori.todo.app

import org.scalatra.ScalatraServlet
import org.scalatra.MethodOverride
import fi.jori.todo.service.TaskService
import org.json4s.DefaultFormats
import org.json4s.Formats
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.CorsSupport
import fi.jori.todo.model.Task

case class TaskContents(topic: String, explanation: String)

class TaskServlet extends ScalatraServlet with MethodOverride with JacksonJsonSupport with CorsSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats
  val service = new TaskService()
  
  before() {
    contentType = formats("json")
  }
  
  private def actionById(action: String => Task): Task = action(params("id"))
  
  options("/*"){
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"))
  }
  
  get("/task/?") {
    service.tasks
  }

  get("/task/:id/?") {
    actionById(service.find)
  }
  
  post("/task/?") {
    val newTask = parsedBody.extract[TaskContents]
    service.create(newTask.topic, newTask.explanation)
  }
  
  put("/task/:id/?") {
    val newTask = parsedBody.extract[TaskContents]
    service.update(params("id"), newTask.topic, newTask.explanation)
  }
  
  put("/task/:id/remove/?") {
    actionById(service.remove)
  }
  
  put("/task/:id/start/?") {
    actionById(service.start)
  }
  
  put("/task/:id/finish/?") {
    actionById(service.finish)
  }
}