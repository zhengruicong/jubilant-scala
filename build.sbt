import sbt.*

lazy val root = (project in file("."))
  .enablePlugins(ScalafmtPlugin, GraalVMNativeImagePlugin, DockerPlugin, JavaServerAppPackaging)
  .settings(
    organization := "com.jubilant",
    name         := "jubilant-scala",
    version      := "0.0.1-SNAPSHOT",
    scalaVersion := Dependencies.Versions.scala,
    Dependencies.dependencies,
    scalafmtOnCompile := true,
    scalacOptions -= "-Xfatal-warnings",
    graalVMNativeImageOptions := Seq(
      "--no-fallback",
      "-H:IncludeResources=application.conf",
      "-H:IncludeResources=.*\\.properties",
      "-H:IncludeResources=.*\\.xdb",
      "-H:IncludeResources=routes",
      "-H:ResourceConfigurationFiles=../../graal/resource-config.json",
      "-H:ReflectionConfigurationFiles=../../graal/reflect-config.json",
      "-H:JNIConfigurationFiles=../../graal/jni-config.json",
      "-H:DynamicProxyConfigurationFiles=../../graal/proxy-config.json",
      "-H:Log=registerResource:5",
      "--enable-url-protocols=https,http",
      "-H:+ReportExceptionStackTraces",
      "--initialize-at-run-time=" + "com.typesafe.config.impl.ConfigImpl$EnvVariablesHolder," + "com.typesafe.config.impl.ConfigImpl$SystemPropertiesHolder"
    ),
    assembly / mainClass := Some("com.jubilant.Main"),
    assembly / assemblyMergeStrategy := {
      case PathList("javax", "servlet", xs @ _*)                                  => MergeStrategy.first
      case PathList("javax", "xml", xs @ _*)                                      => MergeStrategy.first
      case PathList("javax", "activation", xs @ _*)                               => MergeStrategy.first
      case PathList("org", "apache", xs @ _*)                                     => MergeStrategy.first
      case PathList("module-info.class", xs @ _*)                                 => MergeStrategy.discard
      case "META-INF/maven/org.webjars/swagger-ui/pom.properties"                 => MergeStrategy.first
      case "/META-INF/resources/webjars/swagger-ui/4.15.5/swagger-initializer.js" => MergeStrategy.first
      case PathList("META-INF", xs @ _*)                                          => MergeStrategy.discard
      case "logback.xml"                                                          => MergeStrategy.concat
      case "application.conf"                                                     => MergeStrategy.concat
      case x =>
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)
    }
  )
