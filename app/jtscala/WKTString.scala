package jtscala

import com.vividsolutions.jts.io.{ WKBWriter, WKTReader }

object WKTHelper {
  implicit class WKTString(val wkt: String) {
    def toWKB: Array[Byte] = new WKBWriter().write(new WKTReader().read(wkt))
  }  
}

