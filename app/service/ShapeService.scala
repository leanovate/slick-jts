package service

import java.sql.ResultSet
import play.api.db.DB
import scala.concurrent.Future

import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

trait ShapeService {
  def listAll(): Future[List[String]]
  def findByCoordinate(latLng: LatLng): Future[Option[String]]
  def findByBoundingBox(boundingBox: BoundingBox): Future[List[String]]
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
        val result = new RsIterator(resultSet).map(x => x.getString(1))
        result.toList
      })
    }

  def findByCoordinate(latLng: LatLng): Future[Option[String]] =
    Future {
      DB.withConnection(c => {
        // x/y are switched in the DB. y,x is therefore correct
        val p = s"POINT(${latLng.lng},${latLng.lat})"
        val sql = (s"select district_name from shapes where ST_Contains(shape, $p)")
        val resultSet = c.prepareStatement(sql).executeQuery()

        if (resultSet.isAfterLast) None else Some(resultSet.getString(1))
      })
    }

  def findByBoundingBox(boundingBox: BoundingBox): Future[List[String]] = ???
}