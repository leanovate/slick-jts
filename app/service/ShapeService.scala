package service

case class BoundingBox(neLat: Double, neLng: Double, swLat: Double, swLng: Double)

trait ShapeService {

  def listAll(): List[String]

  def findByCoordinate(lat: Double, lng: Double): Option[String]
  def findByBoundingBox(boundingBox: BoundingBox): List[String]
}

class DemoShapeService extends ShapeService {

  def listAll(): List[String] = ???

  def findByCoordinate(lat: Double, lng: Double): Option[String] = ???
  def findByBoundingBox(boundingBox: BoundingBox): List[String] = ???
}