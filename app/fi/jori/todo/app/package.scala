package fi.jori.todo

import com.novus.salat.Context
import play.api.Play
import play.api.Play.current

package object app {
  implicit val ctx = {
    val c = new Context() {
      val name = "Custom Context"
    }
    c.registerClassLoader(Play.classloader)
    c
  }
}