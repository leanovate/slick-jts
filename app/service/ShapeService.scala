package service

import service.Shapes.Shapes
import slick.driver.H2Driver.api._

import jtscala.WKTHelper._
import scala.concurrent.{Await, Future}

trait ShapeService {
  def populate(ls: List[String])

  def listAll(): Future[Seq[String]]
  def findByCoordinate(latLng: LatLng): Future[Option[String]]
  def findByBoundingBox(boundingBox: BoundingBox) // : Future[List[String]]
}

class DemoShapeService extends ShapeService {

  val db = Database.forConfig("shapes")
  val shapes: TableQuery[Shapes] = TableQuery[Shapes]

  def populate(ls: List[String]) = {
    db.withSession { implicit session =>
      org.h2gis.h2spatialext.CreateSpatialExtension.initSpatialExtension(session.conn)
    }

    val data = ls.grouped(3).toList.map({
      case List(id, districtName, wkt) => (id.toInt, districtName, wkt.toWKB)
    })
    val insertAction = shapes ++= data

    val setupAction: DBIO[Unit] = DBIO.seq(
      shapes.schema.create,
      insertAction
    )
    db.run(setupAction)
  }

  val allShapesAction: DBIO[Seq[(Int, String, Array[Byte])]] = shapes.result
  val allNamesAction: DBIO[Seq[String]] = shapes.map(_.districtName).result

  def listAll(): Future[Seq[String]] = db.run(allNamesAction)

  def findByCoordinate(latLng: LatLng): Future[Option[String]] = {
    val wkt = s"POINT(${latLng.lat} ${latLng.lng})"
    val q = s"select district_name from shapes where ST_Contains(shape, $wkt)"
    println(q)

    val sql = sql"select district_name from shapes where ST_Contains(shape, $wkt)".as[String].headOption

    sql.statements.toList.foreach(println _)
    db.run(sql)
  }


  def findByBoundingBox(boundingBox: BoundingBox) = ???
}