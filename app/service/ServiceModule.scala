package service

import com.softwaremill.macwire._

class ServiceModule {
  lazy val shapeService = wire[H2ShapeService]
}
