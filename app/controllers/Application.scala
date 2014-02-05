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
  
  val service = new TaskService()

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def get(uid: String) = Action {
    def task = service.find(uid)
    task match { 
      case None => BadRequest("Error: Task not found")
      case _ => Ok(Json.toJson(task.get)).as(jsonContent)
    }
  }
  
  def tasks = Action {
    def tasks = service.tasks.values
    Ok(Json.arr(tasks)).as(jsonContent)
  }

  def createTask = Action(parse.json) { request =>
    request.body.validate[(String,String)].map { 
      case (topic,explanation) => {
        def task = service.create(Task(topic=topic,explanation=explanation))
        Ok(Json.toJson(task)).as(jsonContent)
      }
    }.recoverTotal {
      e => BadRequest("Error: " + JsError.toFlatJson(e))
    }
  }
  
  def startTask(uid: String) = Action {
    def task = service.start(uid)
    task match { 
      case Task(_,_,_,_,_,_,_) => Ok(Json.toJson(task)).as(jsonContent)
      case _ => BadRequest("Error: Task not found")
    }
  }
  
}