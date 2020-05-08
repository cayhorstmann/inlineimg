import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

enablePlugins(GraalVMNativeImagePlugin)

graalVMNativeImageOptions := Seq("--no-fallback")

lazy val root = (project in file("."))
  .settings(
    name := "inlineimg",
    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.2.0",
    scalacOptions := Seq("-unchecked", "-deprecation")
  )

