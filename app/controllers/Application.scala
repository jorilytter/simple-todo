package controllers

import scala.slick.jdbc.JdbcBackend.Database

import com.mchange.v2.c3p0.ComboPooledDataSource

import fi.jori.todo.model.Task
import fi.jori.todo.service.TaskService
import play.api.Play
import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.JsError
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.json.__
import play.api.mvc.Action
import play.api.mvc.Controller

object Application extends Controller {

  val jsonContent = "application/json"
  implicit val readCreateTask = ((__ \ 'topic).read[String] and (__ \ 'explanation).read[String]) tupled
  implicit val taskJson = Json.writes[Task]
  
  val dbUrl = Play.current.configuration.getString("mysql.url").get
  val user = Play.current.configuration.getString("mysql.user").get
  val pass = Play.current.configuration.getString("mysql.pass").get
  val driver = Play.current.configuration.getString("mysql.driver").get
  
  val database = {
    val ds = new ComboPooledDataSource
    ds.setDriverClass(driver)
    ds.setJdbcUrl(dbUrl)
    ds.setUser(user)
    ds.setPassword(pass)
    Database.forDataSource(ds)
  }
  
  private val service = new TaskService(database)

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
    def formatTasks(allTasks: Seq[Task]) = Json.obj("tasks" -> allTasks.map(task => Json.toJson(task)))
    def tasks = service.find
    println(tasks)
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