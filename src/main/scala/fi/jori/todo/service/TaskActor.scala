package fi.jori.todo.service

import akka.actor.Actor
import spray.routing.HttpService
import spray.httpx.Json4sSupport
import org.json4s.Formats
import org.json4s.DefaultFormats
import spray.http.HttpRequest
import spray.http.HttpMethods
import spray.http.Uri
import spray.http.HttpResponse
import spray.can.Http

class TaskActor extends Actor with Json4sSupport {

  import context.dispatcher
  
  implicit def json4sFormats: Formats = DefaultFormats
  def actorRefFactory = context
  

  def receive = {
    case _: Http.Connected => sender ! Http.Register(self)
    case HttpRequest(HttpMethods.GET, Uri.Path(""),_,_,_) => sender ! HttpResponse(entity = welcome)
    case HttpRequest(HttpMethods.GET, Uri.Path("task"),_,_,_) => sender ! allTasks
  }
  
  val welcome = "welcome to simple todo api"
  def allTasks = List(Nil)
}