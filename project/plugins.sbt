resolvers += Resolver.jcenterRepo

addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % "1.10.1")
addSbtPlugin("com.github.sbt"     % "sbt-release"              % "1.1.0")
addSbtPlugin("org.scalameta"      % "sbt-scalafmt"             % "2.4.2")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.1.0")
addSbtPlugin("io.gatling"         % "gatling-sbt"              % "4.1.6")
addSbtPlugin("org.scoverage"      % "sbt-scoverage"            % "2.0.2")
addSbtPlugin("com.github.sbt"     % "sbt-native-packager"      % "1.9.16")
addSbtPlugin("com.lifeway"        % "sbt-testdocker"           % "1.0.0")
