import controllers.Application

import org.specs2.mutable.Specification

import play.api.test._
import play.api.test.Helpers._
import service.{ LatLng, DemoShapeService }

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ApplicationSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/boum")) must beNone
    }


    "find shape using coordinates" in new WithApplication() {
      Application.populateDB()
      val shapeService = new DemoShapeService()
      val result = Await.result(shapeService.findByCoordinate(LatLng(13.39873, 52.49439)), Duration("100 milliseconds"))
      result must equalTo(Some("Kreuzberg (Kreuzberg)"))
    }
  }
}
