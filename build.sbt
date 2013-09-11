organization := "com.github.havarunner"

name := "havarunner"

version := System.getProperty("releaseVersion", "0.1.0-SNAPSHOT")

crossPaths := false

publishMavenStyle := true

Option(System.getenv("mvnrepo")) match {
  case Some(repoDir) =>
    publishTo := Some(Resolver.file("file",  new File(repoDir)))
  case None =>
    publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))
}

scalaVersion := "2.10.2"

javacOptions ++= Seq("-source", "1.7")

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.11" % "provided, test",
  "com.novocode" % "junit-interface" % "0.9" % "test",
  "com.google.guava" % "guava" % "14.0.1" % "test",
  "com.google.code.findbugs" % "jsr305" % "2.0.1"  % "test"
)

pomExtra :=
  <licenses>
    <license>
      <name>The MIT Licence (MIT)</name>
      <url>http://www.opensource.org/licenses/mit-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:havarunner/havarunner.git</url>
    <connection>scm:git:git@github.com:havarunner/havarunner.git</connection>
    <developerConnection>git@github.com:havarunner/havarunner.git</developerConnection>
  </scm>
  <developers>
    <developer>
      <email>lauri.lehmijoki@iki.fi</email>
      <name>Lauri Lehmijoki</name>
    </developer>
  </developers>
