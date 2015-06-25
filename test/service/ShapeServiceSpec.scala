package service

import org.specs2.mutable.Specification
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.language.implicitConversions

class ShapeServiceSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in new WithApplication {
      route(FakeRequest(GET, "/boum")) must beNone
    }

    "find shape using coordinates" in new WithApplication() {
      val shapeService = new DemoShapeService()
      val result = Await.result(shapeService.findByCoordinate(LatLng(52.49439, 13.39873)), Duration("100 milliseconds"))
      result must equalTo(Some("Kreuzberg"))
    }

    "find no shape using wrong coordinates" in new WithApplication {
      val shapeService = new DemoShapeService()
      val result = Await.result(shapeService.findByCoordinate(LatLng(0.0, 0.0)), Duration("100 milliseconds"))
      result must equalTo(None)
    }

    "find by wrong bounding box results in empty list" in new WithApplication {
      val shapeService: DemoShapeService = new DemoShapeService()
      val result = Await.result(shapeService.findByBoundingBox(BoundingBox(0.0, 0.0, 0.0, 0.0)), Duration("100 milliseconds"))
      result must beEmpty
    }

    "find by extremely large bounding box results in list with one entry" in new WithApplication() {
      val shapeService: DemoShapeService = new DemoShapeService()
      val result = Await.result(shapeService.findByBoundingBox(BoundingBox(55, 10, 50, 15)), Duration("100 milliseconds"))
      result must contain("Kreuzberg")
      result must haveSize(81)
    }

    "find only Rummelsburg by bounding box" in new WithApplication() {
      val shapeService: DemoShapeService = new DemoShapeService()
      val result = Await.result(shapeService.findByBoundingBox(BoundingBox(52.499055862427, 13.4752368927, 52.493432998657, 13.484037399292)), Duration("100 milliseconds"))
      result must haveSize(1)
      result must contain("Rummelsburg")
    }
  }
}
