package fi.jori.todo.actor

import akka.actor.Actor
import akka.actor.Terminated

class DummyActor extends Actor {

  override def receive = {
    case msg: String => println(s"Received secret message: $msg")
  }
}