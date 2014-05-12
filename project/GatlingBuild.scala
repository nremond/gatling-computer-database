import sbt._
import sbt.Keys._

import io.gatling.sbt.GatlingPlugin.{ Gatling, gatlingSettings }


object GatlingBuild extends Build {

  // val gatling = "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.0.0-SNAPSHOT" % "test"
  // val gatlingTestFramework = "io.gatling" % "test-framework" % "1.0-SNAPSHOT" % "test"

  // lazy val project = Project("gatling-computer-database", file("."))
  //                  .settings(gatlingSettings: _*)
  //                  .configs(Gatling)
  //                  .settings(libraryDependencies ++= Seq(gatling, gatlingTestFramework))

 
	val libs = Seq(
		"io.gatling.highcharts" % "gatling-charts-highcharts" % "2.0.0-SNAPSHOT" % "test",
		"io.gatling" % "test-framework" % "1.0-SNAPSHOT" % "test"
		)
	 
	val root = Project("sbt-test", file("."))
			.settings(gatlingSettings: _*)
			.configs(Gatling)
			.settings(organization := "io.gatling.sbt.test")
			.settings(libraryDependencies ++= libs)
			.settings(resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")

}