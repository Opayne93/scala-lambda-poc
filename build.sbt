
import scala.sys.process._
import Path.relativeTo

val Versions = new {
  val circeVersion                = "0.14.1"
  val scalatest                   = "3.2.8"
  val scalactic                   = "3.2.5"
  val eventSourcingInfrastructure = "0.2.128"
  val macrotaskExecutor           = "1.0.0"
  val eventSourcingInfrastructureCdk = "0.3.20"
}

val baseSettings = Seq(
  Test / publishArtifact := false,
  organization := "com.lifeway.account",
  scalaVersion := "2.13.8",
  scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-Xfatal-warnings"),
  resolvers ++= Seq[Resolver](
    "Artifactory LifewayAccount" at "https://artifactory.prod.lifeway.com/artifactory/lifewayaccount/",
    "Artifactory-CTP" at "https://artifactory.prod.lifeway.com/artifactory/contentplatform/"
  ),
  credentials += Credentials(
    "Artifactory Realm",
    "artifactory.prod.lifeway.com",
    sys.env.getOrElse("ARTIFACTORY_LW_USER", "bad user"),
    sys.env.getOrElse("ARTIFACTORY_LW_KEY", "bad key")
  ),
  libraryDependencies ++= Seq(
    "org.scalactic" %% "scalactic" % Versions.scalactic,
    "org.scalatest" %% "scalatest" % Versions.scalatest % Test
  ) ++ Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-parser"
  ).map(_ % Versions.circeVersion),
  publishMavenStyle := true,
  publishTo := {
    if (version.value.trim.endsWith("SNAPSHOT"))
      Some(
        "Artifactory Realm" at
          "https://artifactory.prod.lifeway.com/artifactory/lifewayaccount;build.timestamp=" +
            new java.util.Date().getTime
      )
    else
      Some("Artifactory Realm" at "https://artifactory.prod.lifeway.com/artifactory/lifewayaccount;")
  },
  maintainer := "oliver.payne@lifeway.com"
)

dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.13.0"

lazy val copyPkgJson = TaskKey[Unit](
  label = "copyPkgJson",
  description = "Copies the package.json file from (sub-)project into `.build` directory"
)

lazy val root = project
  .in(file("."))
  .aggregate(
    poc, infrastructure
  )
  .settings(
    baseSettings,
    publish := {},
    publishLocal := {},
    name := "scala-lambda-poc"
  )

lazy val poc = project
  .in(file("poc"))
  .enablePlugins(JavaAppPackaging)
  .settings(
    baseSettings,
    libraryDependencies ++= Seq(
      "io.github.mkotsur"             %% "aws-lambda-scala"                                      % "0.3.0",
      "io.github.crac"                 % "org-crac"                                              % "0.1.3",
      "com.lifeway.consumersolutions" %% "consumersolutions-eventsourcing-infrastructure-common" % Versions.eventSourcingInfrastructure
    ),
    publish / skip := true,
    Universal / target := baseDirectory.value / ".build/",
    Universal / packageName := "poc",
    /**
      * Native Packager config for AWS Lambda:
      * 1.) no top level directory in the zip
      * 2.) this app itself should not be jar'd, the classes and resources should be at the top dir
      * 3.) No docs are needed - just the app + jars
      * 4.) All dependency jars in /lib EXCEPT this apps jar itself (which is built by native packager)
      */
    topLevelDirectory := None,
    Universal / mappings ++= {
      (Compile / packageBin).value
      val t   = target.value
      val dir = t / "scala-2.13" / "classes"
      (dir.allPaths --- dir) pair relativeTo(dir)
    },
    mappings in (Compile, packageDoc) := Seq(),
    addCommandAlias("poc-build", "poc/universal:packageBin"),
    addCommandAlias("poc-clean", "poc/clean"),
    addCommandAlias("poc-test", "poc/scalafmtCheckAll; poc/test;")
  )


val infrastructure = project
  .in(file("infrastructure"))
  .dependsOn(poc)
  .settings(
    baseSettings,
    libraryDependencies ++= Seq(
      "com.lifeway.consumersolutions" %% "consumersolutions-eventsourcing-infrastructure-cdk" % Versions.eventSourcingInfrastructureCdk,
      "software.amazon.awscdk" % "aws-cdk-lib"                     % "2.45.0",
      "software.amazon.awscdk" % "apigatewayv2-alpha"              % "2.45.0-alpha.0",
      "software.amazon.awscdk" % "apigatewayv2-integrations-alpha" % "2.45.0-alpha.0",
    ),
    addCommandAlias(
      "infrastructure-build",
      "domain/fullOptJS; domain/copyPkgJson;",
    ),
  )

/* releaseVersionFile := file("version.sbt") */
/* releaseCrossBuild := true */
/* releaseTagName := s"${(ThisBuild / version).value}" */
/* releaseProcess := Seq[ReleaseStep]( */
/*   checkSnapshotDependencies, */
/*   inquireVersions, */
/*   runClean, */
/*   runTest, */
/*   setReleaseVersion, */
/*   commitReleaseVersion, */
/*   tagRelease, */
/*   publishArtifacts, */
/*   setNextVersion, */
/*   commitNextVersion, */
/*   pushChanges */
/* ) */
