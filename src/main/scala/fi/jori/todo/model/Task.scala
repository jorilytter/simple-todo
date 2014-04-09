package fi.jori.todo.model

import java.util.Date

case class Task(id: Option[String] = None,
    topic: String,
    explanation: String,
    created: Option[Date] = None,
    started: Option[Date] = None,
    finished: Option[Date] = None,
    deleted: Option[Date] = None) {}