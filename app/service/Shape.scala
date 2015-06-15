package service;

import slick.driver.H2Driver.api._

object Shapes {

  class Shapes(tag: Tag) extends Table[(Int, String, Array[Byte])](tag, "SHAPES") {
    def id = column[Int]("ID", O.PrimaryKey)
    def districtName = column[String]("DISTRICT_NAME")
    def shape = column[Array[Byte]]("SHAPE")

    def * = (id, districtName, shape)
  }
}