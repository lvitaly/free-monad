ThisBuild / scalaVersion := "3.3.5"
ThisBuild / organization := "com.lvitaly.fm"

lazy val rootProject = (project in file(".")).settings(
  Seq(
    name    := "free-monad-example",
    version := "0.1.0-SNAPSHOT",
    libraryDependencies ++= catsKit ++ catsEffectKit ++ http4sKit ++ commonKit
  )
)

lazy val catsKit = Seq(
  "org.typelevel" %% "cats-core",
  "org.typelevel" %% "cats-free"
).map(_ % "2.13.0")

lazy val catsEffectKit = Seq(
  "org.typelevel" %% "cats-effect"
).map(_ % "3.5.7")

lazy val http4sKit = Seq(
  "org.http4s" %% "http4s-dsl",
  "org.http4s" %% "http4s-ember-server"
).map(_ % "0.23.30")

lazy val commonKit = Seq(
  "ch.qos.logback" % "logback-classic" % "1.5.16"
)
