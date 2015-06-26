package controllers

import com.softwaremill.macwire._
import service.ServiceModule

trait ControllerModule extends ServiceModule {
  lazy val findController = wire[FindController]
}
