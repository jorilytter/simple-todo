package fi.jori.todo.model

import java.sql.Date
import scala.slick.driver.MySQLDriver.simple._

class TaskDAL() {

  type taskPhase = (Boolean, Boolean, Boolean)
  
  val tasks = TableQuery[Tasks]

  def getDate = Some(new Date(System.currentTimeMillis()))
  
  def getPhase(id: String)(implicit session: Session) = {
    def task = find(id)
    (task.started == None, task.finished == None,task.deleted == None)
  }
  
  def find(implicit session: Session): List[Task] = tasks.run.toList
  
  def find(id: String)(implicit session: Session) = filterById(id).first
  
  def create(task: Task)(implicit session: Session) = tasks += task
  
  def start(id: String)(implicit session: Session) = {
    def task = find(id)
    if (task.started == None) filterById(id) map (t => (t.started)) update (getDate)
  }
  
  def finish(id: String)(implicit session: Session) = {
    
    def task = find(id)
    if (task.started == None) filterById(id) map (t => (t.started,t.finished)) update (getDate,getDate)
    else if (task.finished == None) filterById(id) map (t => (t.finished)) update (getDate)
  }
  
  def remove(id: String)(implicit session: Session) = filterById(id) map (t => t.deleted) update (getDate)
  
  def update(id: String, topic: String, explanation: String)(implicit session: Session) = {
    filterById(id) map (t => (t.topic,t.explanation)) update (topic,explanation)
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