name := "scaml-doc"

version := "0.1-SNAPSHOT"

organization := "org.scaml"

description := "Generates PDFs with ScaML"

licenses += ("Apache-2.0" -> url("http://opensource.org/licenses/Apache-2.0"))

scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.11.7")

scalacOptions ++= Seq("-deprecation", "-feature", "-Xlint", "-deprecation")


// add scala-xml dependency when needed (for Scala 2.11 and newer)
// this mechanism supports cross-version publishing
libraryDependencies ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, scalaMajor)) if scalaMajor >= 11 =>
      Seq("org.scala-lang.modules" %% "scala-xml" % "1.0.5")
    case _ =>
      Nil
  }
}


libraryDependencies += "org.scaml" %% "scaml" % "0.3.0.28"
resolvers += "ScaML Bintray Repo" at "https://bintray.com/artifact/download/jentsch/maven/"

libraryDependencies += "org.apache.xmlgraphics" % "fop" % "2.0"

// Tests
libraryDependencies += "org.specs2" %% "specs2-core" % "3.6.4" % "test"
resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

scalacOptions in Test += "-Yrangepos"

