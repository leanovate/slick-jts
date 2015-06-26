package service

import com.softwaremill.macwire._

trait ServiceModule {
  lazy val shapeService = wire[H2ShapeService]
}
