import fi.jori.todo.app._
import org.scalatra._
import javax.servlet.ServletContext
import _root_.akka.actor.ActorSystem
import fi.jori.todo.actor.DummyActor
import _root_.akka.actor.Props
import _root_.akka.actor.Terminated
import _root_.akka.routing.SmallestMailboxPool
import scala.concurrent.duration._
import java.util.concurrent.CountDownLatch

class ScalatraBootstrap extends LifeCycle {

  private val system = ActorSystem()
  private val dummyActorRef = system.actorOf(Props[DummyActor])

  override def init(context: ServletContext) {
    context.mount(new TaskServlet(system, dummyActorRef), "/*")
  }

  override def destroy(context: ServletContext) {
    system.shutdown()
    system.awaitTermination()
  }
}
