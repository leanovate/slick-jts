package service

import java.sql.ResultSet
import play.api.db.DB
import jtscala.WKTHelper._
import scala.concurrent.Future

import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

trait ShapeService {
  def listAll(): Future[List[String]]

  def findByCoordinate(latLng: LatLng): Future[Option[String]]

  def findByBoundingBox(boundingBox: BoundingBox): Future[Seq[String]]
}

class DemoShapeService extends ShapeService {

  class RsIterator(rs: ResultSet) extends Iterator[ResultSet] {
    def hasNext: Boolean = rs.next()

    def next(): ResultSet = rs
  }

  def listAll(): Future[List[String]] =
    Future {
      DB.withConnection(c => {
        val resultSet = c.prepareStatement("select district_name from shapes").executeQuery()
        val result = new RsIterator(resultSet).map(row => row.getString(1))
        result.toList
      })
    }

  def findByCoordinate(latLng: LatLng): Future[Option[String]] =
    Future {
      DB.withConnection(c => {
        val wkt = s"POINT(${latLng.lat} ${latLng.lng})"

        val sql = "select district_name from shapes where ST_Contains(shape, ?)"
        val stmt = c.prepareStatement(sql)
        stmt.setWKT(1, wkt)
        val resultSet = stmt.executeQuery()

        if (resultSet.next()) Some(resultSet.getString(1)) else None
      })
    }

  def findByBoundingBox(boundingBox: BoundingBox): Future[Seq[String]] = Future {
    DB.withConnection(c => {
      val wkt = s"POLYGON((${boundingBox.upperLeftLat} ${boundingBox.upperLeftLng}, ${boundingBox.lowerRightLat} ${boundingBox.upperLeftLng}, ${boundingBox.lowerRightLat} ${boundingBox.lowerRightLng}, ${boundingBox.upperLeftLat} ${boundingBox.lowerRightLng}, ${boundingBox.upperLeftLat} ${boundingBox.upperLeftLng}))"

      val sql = "select district_name from shapes where ST_Contains(shape, ?)"
      val stmt = c.prepareStatement(sql)
      stmt.setWKT(1, wkt)
      val resultSet = stmt.executeQuery()

      if (resultSet.next)
        nextValue(resultSet)
      else
        Seq()
    })
  }

  private def nextValue(resultSet: ResultSet): Seq[String] = {
    val districtName: String = resultSet.getString(1)
    if (resultSet.next)
      Seq(districtName) ++ nextValue(resultSet)
    else
      Seq(districtName)
  }
}