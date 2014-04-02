package fi.jori.todo.model

import java.sql.Date
import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.JdbcBackend.DatabaseDef

class TaskDAL(db: DatabaseDef) {
 
  val tasks = TableQuery[Tasks]

  def getDate = Some(new Date(System.currentTimeMillis()))
  
  def find: List[Task] = db withSession { 
    implicit session => tasks.run.toList 
  }
  
  def removedTasks = db.withSession {
    implicit session => {
      (tasks filter(t => (t.deleted.isNotNull))).run.toList
    }
  }
  
  def finishedTasks = db.withSession {
    implicit session => {
      (tasks filter(t => (t.deleted.isNull && t.finished.isNotNull))).run.toList
    }
  }
  
  def startedTasks = db.withSession {
    implicit session => {
      (tasks filter(t => (t.deleted.isNull && t.finished.isNull && t.started.isNotNull))).run.toList
    }
  }
  
  def createdTasks = db.withSession {
    implicit session => {
      (tasks filter(t => (t.deleted.isNull && t.finished.isNull && t.started.isNull))).run.toList
    }
  }
  
  def find(id: String) = db withSession { 
    implicit session => filterById(id).first 
  }
  
  def create(task: Task) = db withSession { 
    implicit session => tasks += task 
  }
  
  def start(id: String) = db withSession { 
    implicit session => {
        def task = find(id)
        if (task.started == None) filterById(id) map (t => (t.started)) update (getDate)
  }}
  
  def finish(id: String) = db withSession { 
    implicit session => {
        def task = find(id)
        if (task.started == None) filterById(id) map (t => (t.started, t.finished)) update (getDate, getDate)
        else if (task.finished == None) filterById(id) map (t => (t.finished)) update (getDate)
  }}
  
  def remove(id: String) = db withSession { 
    implicit session => filterById(id) map (t => t.deleted) update (getDate) 
  }
  
  def update(id: String, topic: String, explanation: String) = db withSession { 
    implicit session => filterById(id) map (t => (t.topic,t.explanation)) update (topic,explanation)
  }
  
  private def filterById(id: String) = tasks filter(t => t.id === id)
}

case class Task(
    id: Option[String] = None,
    topic: String,
    explanation: String,
    created: Option[Date] = None,
    started: Option[Date] = None,
    finished: Option[Date] = None,
    deleted: Option[Date] = None) {}

  class Tasks(tag: Tag) extends Table[Task](tag, "tasks") {

    def id = column[Option[String]]("id", O.PrimaryKey)
    def topic = column[String]("topic")
    def explanation = column[String]("description")
    def created = column[Option[Date]]("created", O.Nullable)
    def started = column[Option[Date]]("started", O.Nullable)
    def finished = column[Option[Date]]("finished", O.Nullable)
    def deleted = column[Option[Date]]("deleted", O.Nullable)
    def * = (id,topic, explanation, created, started, finished, deleted) <> (Task.tupled, Task.unapply _)
  }