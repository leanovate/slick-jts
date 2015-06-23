import controllers.Application

import org.specs2.mutable.Specification

import play.api.test._
import play.api.test.Helpers._
import service.{BoundingBox, LatLng, DemoShapeService}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.language.implicitConversions

class ApplicationSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/boum")) must beNone
    }

    "find shape using coordinates" in new WithApplication() {
      Application.populateDB()
      val shapeService = new DemoShapeService()
      val result = Await.result(shapeService.findByCoordinate(LatLng(52.49439, 13.39873)), Duration("100 milliseconds"))
      result must equalTo(Some("Kreuzberg"))
    }

    "find no shape using wrong coordinates" in new WithApplication() {
      Application.populateDB()
      val shapeService = new DemoShapeService()
      val result = Await.result(shapeService.findByCoordinate(LatLng(0.0, 0.0)), Duration("100 milliseconds"))
      result must equalTo(None)
    }

    "find by wrong bounding box results in empty list" in new WithApplication() {
      Application.populateDB()
      val shapeService: DemoShapeService = new DemoShapeService()
      val result = Await.result(shapeService.findByBoundingBox(BoundingBox(0.0, 0.0, 0.0, 0.0)), Duration("100 milliseconds"))
      result must be empty
    }

    "find by extremely large bounding box results in list with one entry" in new WithApplication() {
      Application.populateDB()
      val shapeService: DemoShapeService = new DemoShapeService()
      val result = Await.result(shapeService.findByBoundingBox(BoundingBox(50, 10, 55, 15)), Duration("100 milliseconds"))
      result must contain("Kreuzberg")
    }

    "find by extremely small bounding box results in list with one entry" in new WithApplication() {
      Application.populateDB()
      val shapeService: DemoShapeService = new DemoShapeService()
      val result = Await.result(shapeService.findByBoundingBox(BoundingBox(52.49439, 13.39873, 52.49438, 13.39872)), Duration("100 milliseconds"))
      result must contain("Kreuzberg")
    }
  }
}
