package jtscala

import java.sql.PreparedStatement

import com.vividsolutions.jts.io.{ WKBWriter, WKTReader }

object WKTHelper {
  implicit class WKTString(val wkt: String) {
    def toWKB: Array[Byte] = new WKBWriter().write(new WKTReader().read(wkt))
  }

  implicit class WKTPreparedStatement(val stmt: PreparedStatement) {
    def setWKT(parameterIndex: Int, wkt: String) = stmt.setBytes(parameterIndex, wkt.toWKB)
  }
}

