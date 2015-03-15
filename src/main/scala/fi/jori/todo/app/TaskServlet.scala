package fi.jori.todo.app

import org.scalatra.ScalatraServlet
import org.scalatra.MethodOverride
import fi.jori.todo.service.TaskService
import org.json4s.DefaultFormats
import org.json4s.Formats
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.CorsSupport
import fi.jori.todo.model.Task
import akka.actor.ActorSystem
import akka.actor.ActorRef

case class TaskContents(topic: String, explanation: String)

class TaskServlet(system: ActorSystem, dummyActor: ActorRef) extends ScalatraServlet with MethodOverride with JacksonJsonSupport with CorsSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats
  val service = new TaskService()
  val defaultResponse = "welcome to the wild side"

  before() {
    contentType = formats("json")
  }

  private def actionById(action: String => Task): Task = action(params("id"))

  options("/*") {
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"))
  }

  get("/") {
    dummyActor ! "I'm coming, don't leave without me!!"
    defaultResponse
  }

  get("/task/?") {
    service.tasks.values.toList
  }

  get("/task/:id/?") {
    actionById(service.find)
  }

  get("/task/todo/?") {
    service.todo
  }

  get("/task/ongoing/?") {
    service.ongoing
  }

  get("/task/done/?") {
    service.done
  }

  get("/task/removed/?") {
    service.removed
  }

  post("/task/?") {
    val newTask = parsedBody.extract[TaskContents]
    service.create(newTask.topic, newTask.explanation)
  }

  put("/task/:id/?") {
    val updateTask = parsedBody.extract[TaskContents]
    service.update(params("id"), updateTask.topic, updateTask.explanation)
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