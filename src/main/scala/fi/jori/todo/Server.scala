package fi.jori.todo

import akka.actor.ActorSystem
import akka.actor.Props
import fi.jori.todo.service.TaskActor
import akka.io.IO
import spray.can.Http

object Server extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem()

  // create and start our service actor
  val service = system.actorOf(Props[TaskActor], "tasks")

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ! Http.Bind(service, interface = "localhost", port = 8080)
 

}