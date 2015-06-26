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
      val shapeService = new H2ShapeService()
      val result = Await.result(shapeService.findByCoordinate(LatLng(52.49439, 13.39873)), Duration("100 milliseconds"))
      result must equalTo(Some("Friedrichshain-Kreuzberg"))
    }

    "find no shape using wrong coordinates" in new WithApplication with ServiceModule {
      val result = Await.result(shapeService.findByCoordinate(LatLng(0.0, 0.0)), Duration("100 milliseconds"))
      result must equalTo(None)
    }

    "find by wrong bounding box results in empty list" in new WithApplication with ServiceModule {
      val result = Await.result(shapeService.findByBoundingBox(BoundingBox(0.0, 0.0, 0.0, 0.0)), Duration("100 milliseconds"))
      result must beEmpty
    }

    "find all boroughs by extremely large bounding box" in new WithApplication with ServiceModule {
      val result = Await.result(shapeService.findByBoundingBox(BoundingBox(55, 10, 50, 15)), Duration("100 milliseconds"))
      result must contain("Friedrichshain-Kreuzberg")
      result must haveSize(12)
    }

    "find only Mitte by bounding box" in new WithApplication with ServiceModule {
      val result = Await.result(shapeService.findByBoundingBox(BoundingBox(52.49873648364294, 13.42940239358947, 52.56773557617564, 13.30153005428186)), Duration("100 milliseconds"))
      result must haveSize(1)
      result must contain("Mitte")
    }
  }
}
