package service

import java.sql.ResultSet
import com.vividsolutions.jts.io.{ WKTReader, WKBWriter }
import play.api.db.DB
import scala.concurrent.Future

import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

trait ShapeService {
  def listAll(): Future[List[String]]
  def findByCoordinate(latLng: Point): Future[Option[String]]
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
        val result = new RsIterator(resultSet).map(row => row.getString(1))
        result.toList
      })
    }

  def findByCoordinate(latLng: Point): Future[Option[String]] =
    Future {
      DB.withConnection(c => {
        // x/y are switched in the DB. y,x is therefore correct
        val p = s"POINT(${latLng.lng} ${latLng.lat})"
        val wkb = new WKBWriter().write(new WKTReader().read(p))

        val sql = "select district_name from shapes where ST_Contains(shape, ?)"
        val stmt = c.prepareStatement(sql)
        stmt.setBytes(1, wkb)
        val resultSet = stmt.executeQuery()

        println(resultSet)

        if (resultSet.next()) Some(resultSet.getString(1)) else None
      })
    }

  def findByBoundingBox(boundingBox: BoundingBox): Future[List[String]] = ???
}