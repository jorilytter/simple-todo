package controllers

import org.bson.types.ObjectId

import fi.jori.todo.model.Task
import fi.jori.todo.service.TaskService
import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.Format
import play.api.libs.json.JsError
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.json.__
import play.api.mvc.Action
import play.api.mvc.Controller

object Application extends Controller {
  
  val jsonContent = "application/json"
    
  implicit val objectIdFormat: Format[ObjectId] = new Format[ObjectId] {
    def reads(json: JsValue) = {
      json match {
        case jsString: JsString => {
          if ( ObjectId.isValid(jsString.value) ) JsSuccess(new ObjectId(jsString.value))
          else JsError("Invalid ObjectId")
        }
        case other => JsError("Can't parse json path as an ObjectId. Json content = " + other.toString())
      }
    }
    def writes(oId: ObjectId): JsValue = {
      JsString(oId.toString)
    }
  }
    
  implicit val readCreateTask = ((__ \ 'topic).read[String] and (__ \ 'explanation).read[String]) tupled
  implicit val taskJson = Json.writes[Task]
  
  private val service = new TaskService()

  private def taskResponse(task: Task) = {
    task match { 
      case Task(_,_,_,_,_,_,_) => Ok(Json.toJson(task)).as(jsonContent)
      case _ => BadRequest("Error: Task not found")
    }
  }
  
  private def formatTasks(allTasks: Iterable[Task]) = 
    Json.obj("tasks" -> allTasks.map(task => Json.toJson(task)))
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def get(uid: String) = Action {
    def task = service.find(uid)
    taskResponse(task)
  }
  
  def tasks = Action {
    def tasks = Task.all
    Ok(formatTasks(tasks)).as(jsonContent)
  }
  
  def createdTasks = Action {
    def tasks = Task.createdTasks
    Ok(formatTasks(tasks)).as(jsonContent)
  }

  def createTask = Action(parse.json) { request =>
    request.body.validate[(String,String)].map { 
      case (topic,explanation) => {
        def task = service.create(topic,explanation)
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