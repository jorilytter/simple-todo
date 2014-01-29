package fi.jori.todo.model

import java.util.Date

case class Task(id: Option[String] = None,
    topic: String,
    explanation: String,
    todo: Option[Date] = None,
    ongoing: Option[Date] = None,
    finished: Option[Date] = None,
    deleted: Option[Date] = None) {

}