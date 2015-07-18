
name := "scaml-pdf"

description := "Generates PDFs with ScaML"

organization := "org.scaml"

version := "1.0"

scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.10.5", "2.11.7")

libraryDependencies += "org.scaml" %% "scaml" % "0.3.0-SNAPSHOT"

libraryDependencies += "org.apache.xmlgraphics" % "fop" % "2.0"

// Tests
resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies += "org.specs2" %% "specs2-core" % "3.6.2" % "test"

scalacOptions in Test += "-Yrangepos"
    