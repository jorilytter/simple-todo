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
  
  private def formatTasks(allTasks: Iterable[Task]) = {
    def tasks = Json.obj("tasks" -> allTasks.map(task => Json.toJson(task)))
    Ok(tasks).as(jsonContent).withHeaders(ACCESS_CONTROL_ALLOW_ORIGIN -> "*")
  }
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def get(uid: String) = Action {
    taskResponse(service.find(uid))
  }
  
  def tasks = Action {
    formatTasks(Task.all)
  }
  
  def createdTasks = Action {
    formatTasks(Task.createdTasks)
  }
  
  def startedTasks = Action {
    formatTasks(Task.startedTasks)
  }
  
  def finishedTasks = Action {
    formatTasks(Task.finishedTasks)
  }
  
  def removedTasks = Action {
    formatTasks(Task.removedTasks)
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
    taskResponse(service.start(uid))
  }
  
  def finishTask(uid: String) = Action {
    taskResponse(service.finish(uid))
  }
  
  def removeTask(uid: String) = Action {
    taskResponse(service.remove(uid))
  }
}