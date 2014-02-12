package fi.jori.todo.model

import java.util.Date

import org.bson.types.ObjectId

import com.mongodb.casbah.Imports.wrapDBObj
import com.mongodb.casbah.MongoConnection
import com.novus.salat.global.ctx
import com.novus.salat.grater

case class Task(_id: Option[ObjectId] = None,
    topic: String,
    explanation: String,
    created: Option[Date] = None,
    started: Option[Date] = None,
    finished: Option[Date] = None,
    deleted: Option[Date] = None) {}

object Tasks {
  
  val tasks = MongoConnection()("simple-todo")("tasks")
  
  def all = tasks.map(grater[Task].asObject(_)).toList
  
  def create(task: Task): Task = { 
    val createdTask = grater[Task].asDBObject(task)
    tasks += createdTask
    grater[Task].asObject(createdTask)
  }
}