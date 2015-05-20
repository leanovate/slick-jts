import com.vividsolutions.jts.{ geom => jts }

import scala.collection.mutable

package object jtscala {
  implicit def tupleToPoint(t:(Double,Double)):Point =
    Point(t._1,t._2)

  implicit def coordinateToPoint(c:jts.Coordinate):Point =
    Point(c.x,c.y)

  implicit def tupleSetToPointSet(ts:Set[(Double,Double)]):Set[Point] =
    ts map(t => Point(t._1,t._2))

  implicit def tupleListToPointList(tl:List[(Double,Double)]):List[Point] =
    tl map(t => Point(t._1,t._2))

  // implicit def tupleArrayToPointList(tl:Array[(Double,Double)]):List[Point] =
  //   tl map(t => Point(t._1,t._2)) toList

  implicit def pointListToCoordinateArray(ps:List[Point]):Array[jts.Coordinate] =
    ps map(p => new jts.Coordinate(p.x,p.y)) toArray

 implicit def multiPointToSetPoint(mp:jts.MultiPoint):Set[Point] = {
    val len = mp.getNumGeometries
    (for(i <- 0 until len) yield {
      Point(mp.getGeometryN(i).asInstanceOf[jts.Point])
    }).toSet
  }

  implicit def multiLineToSetLine(ml:jts.MultiLineString):Set[Line] = {
    val len = ml.getNumGeometries
    (for(i <- 0 until len) yield {
      Line(ml.getGeometryN(i).asInstanceOf[jts.LineString])
    }).toSet
  }

  implicit def multiPolygonToSetPolygon(mp:jts.MultiPolygon):Set[Polygon] = {
    val len = mp.getNumGeometries
    (for(i <- 0 until len) yield {
      Polygon(mp.getGeometryN(i).asInstanceOf[jts.Polygon])
    }).toSet
  }

  implicit def geometryCollectionToSetGeometry(gc:jts.GeometryCollection):Set[Geometry] = {
    val len = gc.getNumGeometries
    (for(i <- 0 until len) yield {
      gc.getGeometryN(i) match {
        case p:jts.Point => Set[Geometry](Point(p))
        case mp:jts.MultiPoint => multiPointToSetPoint(mp)
        case l:jts.LineString => Set[Geometry](Line(l))
        case ml:jts.MultiLineString => multiLineToSetLine(ml)
        case p:jts.Polygon => Set[Geometry](Polygon(p))
        case mp:jts.MultiPolygon => multiPolygonToSetPolygon(mp)
        case gc:jts.GeometryCollection => geometryCollectionToSetGeometry(gc)
      }
    }).toSet.flatten
  }

  implicit def seqPointToPointSet(ps:Set[Point]):PointSet = PointSet(ps)
  implicit def seqLineToLineSet(ps:Set[Line]):LineSet = LineSet(ps)
  implicit def seqPolygonToPolygonSet(ps:Set[Polygon]):PolygonSet = PolygonSet(ps)

  implicit def seqGeometryToGeometryCollection(gs:Set[Geometry]):GeometryCollection = {
    val points = mutable.Set[Point]()
    val lines = mutable.Set[Line]()
    val polygons = mutable.Set[Polygon]()
    for(g <- gs) { 
      g match {
        case p:Point => points += p
        case l:Line => lines += l
        case p:Polygon => polygons += p
        case _ => sys.error(s"Unknown Geometry type: $g")
      }
    }
    GeometryCollection(points.toSet,lines.toSet,polygons.toSet)
  }
}
