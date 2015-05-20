package service

import scala.concurrent.Future

case class BoundingBox(neLat: Double, neLng: Double, swLat: Double, swLng: Double)

trait ShapeService {

  def listAll(): Future[List[String]]

  def findByCoordinate(lat: Double, lng: Double): Future[Option[String]]
  def findByBoundingBox(boundingBox: BoundingBox): Future[List[String]]
}

class DemoShapeService extends ShapeService {

  def listAll(): Future[List[String]] = {
    Future.successful(List("Munnerum", "Oppau"))
  }

  def findByCoordinate(lat: Double, lng: Double): Future[Option[String]] = ???
  def findByBoundingBox(boundingBox: BoundingBox): Future[List[String]] = ???
}