package controllers

import play.api.libs.json.Json
import play.api.libs.json.__
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.Action
import play.api.mvc.Controller
import views.html.defaultpages.badRequest
import play.api.libs.json.JsError
import play.api.libs.functional.syntax._
import fi.jori.todo.service.TaskService
import fi.jori.todo.model.Task

object Application extends Controller {

  val jsonContent = "application/json"
  implicit val readCreateTask = ((__ \ 'topic).read[String] and (__ \ 'explanation).read[String]) tupled
  implicit val taskJson = Json.writes[Task]
  
  private val service = new TaskService()

  private def taskResponse(task: Task) = {
    task match { 
      case Task(_,_,_,_,_,_,_) => Ok(Json.toJson(task)).as(jsonContent)
      case _ => BadRequest("Error: Task not found")
    }
  }
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def get(uid: String) = Action {
    def task = service.find(uid)
    taskResponse(task)
  }
  
  def tasks = Action {
    
    def formatTasks(allTasks: Iterable[Task]) = Json.obj("tasks" -> allTasks.map(task => Json.toJson(task)))
    
    def tasks = service.tasks.values
    Ok(formatTasks(tasks)).as(jsonContent)
  }

  def createTask = Action(parse.json) { request =>
    request.body.validate[(String,String)].map { 
      case (topic,explanation) => {
        def task = service.create(Task(topic=topic,explanation=explanation))
        taskResponse(task)
      }
    }.recoverTotal {
      e => BadRequest("Error: " + JsError.toFlatJson(e))
    }
  }
  
  def startTask(uid: String) = Action {
    def task = service.start(uid)
    taskResponse(task)
  }
  
  def finishTask(uid: String) = Action {
    def task = service.finish(uid)
    taskResponse(task)
  }
  
  def removeTask(uid: String) = Action {
    def task = service.remove(uid)
    taskResponse(task)
  }
}