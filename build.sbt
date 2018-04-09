name := "scalaCoinBetting"


lazy val server = (project in file("server")).settings(commonSettings).settings(
  logLevel := Level.Error,
  scalaJSProjects := Seq(client),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  pipelineStages := Seq(digest, gzip),
  // triggers scalaJSPipeline when using compile or continuous compilation
  compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
  libraryDependencies ++= Seq(
    "com.vmunier" %% "scalajs-scripts" % "1.1.1",
    guice,
    specs2 % Test,
    ws,
    ehcache,
    "com.typesafe.play" %% "play-slick" % "3.0.1",
    "com.typesafe.play" %% "play-slick-evolutions" % "3.0.1",
    "com.h2database" % "h2" % "1.4.196",
    "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",
    "com.softwaremill.common" %% "tagging" % "2.2.0",
    "com.softwaremill.macwire" %% "macrosakka" % "2.3.0" % "provided",
    "com.dripower" %% "play-circe" % "2609.0",
    "io.circe" %% "circe-generic-extras" % "0.9.0",
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
  ),
  scalafmtOnCompile in ThisBuild := true, // all projects
  scalafmtOnCompile := true, // current project
  scalafmtOnCompile in Compile := true, // current project, specific configuration
  // Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
  EclipseKeys.preTasks := Seq(compile in Compile)
).enablePlugins(PlayScala).
  dependsOn(sharedJvm)

lazy val client = (project in file("client")).settings(commonSettings).settings(
  scalacOptions ++= Seq(
    "-P:scalajs:sjsDefinedByDefault",
  ),

  resolvers += Resolver.jcenterRepo,

  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.4",
    "in.nvilla" %%% "monadic-html" % "0.4.0-RC1",
    "io.circe" %%% "circe-core" % "0.9.0",
    "io.circe" %%% "circe-generic" % "0.9.0",
    "io.circe" %%% "circe-parser" % "0.9.0",
    "io.circe" %%% "circe-generic-extras" % "0.9.0",
    "be.doeraene" %%% "scalajs-jquery" % "0.9.2",
    "com.github.karasiq" %%% "scalajs-highcharts" % "1.2.1",
    "com.lihaoyi" %%% "upickle" % "0.5.1",
    "com.lihaoyi" %%% "scalarx" % "0.3.2",
    // "com.definitelyscala" %%% "scala-js-materializecss" % "1.0.2",
  ),

  scalaJSUseMainModuleInitializer := true,
  skip in packageJSDependencies := false,
  // jsDependencies += "org.webjars" % "jquery" % "3.2.1" / "3.2.1/jquery.js",
).enablePlugins(ScalaJSPlugin, ScalaJSWeb).
  dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
  .settings(commonSettings)
  .settings(libraryDependencies ++= Seq(
    "io.circe" %% "circe-core" % "0.9.0",
    "io.circe" %% "circe-generic" % "0.9.0",
    "io.circe" %% "circe-parser" % "0.9.0",
    "io.circe" %% "circe-generic-extras" % "0.9.0",
  ))
lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

lazy val commonSettings = Seq(
  scalaVersion := "2.12.4",
  organization := "$organization$"
)

// loads the server project at sbt startup
onLoad in Global := (onLoad in Global).value andThen { s: State => "project server" :: s }