name := """slick-spatial"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,

  "com.vividsolutions" % "jts" % "1.13"
)

// https://groups.google.com/forum/#!topic/play-framework/Efh1am71XtA

val commonClassLoader: Classpath => ClassLoader = {
  var common: ClassLoader = null
  classpath => {
    if (common == null) {
      val jars = classpath.map(_.data).collect {
        case jar if jar.getName.startsWith("h2-") || jar.getName == "h2.jar" ||
          jar.getName.startsWith("h2gis") || jar.getName.startsWith("jts") => jar.toURI.toURL
      }
      common = new java.net.URLClassLoader(jars.toArray, null)
    }
    common
  }
}

play.Play.playCommonClassloader := {
  val classpath = (dependencyClasspath in Compile).value
  commonClassLoader(classpath)
}

fork in run := true