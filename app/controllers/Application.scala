package controllers

import play.api.libs.json.Json
import play.api.libs.json.__
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.Action
import play.api.mvc.Controller
import views.html.defaultpages.badRequest
import play.api.libs.json.JsError
import play.api.libs.functional.syntax._

object Application extends Controller {

  val jsonContent = "application/json"
  type taskType = (String, String)
  var allTasks: List[taskType] = List.empty
  
  private def newTask(topic: String) = {
    println("adding new task "+topic)
    allTasks = (java.util.UUID.randomUUID().toString(), topic) :: allTasks
  }
  
  private def formatTasks = for(task <- allTasks) yield Json.obj("id" -> task._1, "topic" -> task._2)
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def tasks = Action {
    def todoTasks = Json.obj("keys" -> Json.toJson(List("eka","toka","kolmas")))
    Ok(todoTasks).as(jsonContent)
  }

  implicit val readCreateTask = ((__ \ 'topic).read[String])
  
  def createTask = Action(parse.json) { request =>
    println(request.body.toString)
    request.body.validate[String].map { 
      case topic => {
        newTask(topic)
        println(allTasks.length)
        def result = Json.obj("tasks" -> formatTasks)
        Ok(result).as(jsonContent)
      }
    }.recoverTotal {
      e => BadRequest("Error: " + JsError.toFlatJson(e))
    }
  }
}