


import java.net.URL

//resolvers += Resolver.url("Sonatype OSS Snapshots", new URL("https://oss.sonatype.org/content/repositories/snapshots"))(Resolver.ivyStylePatterns)

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

addSbtPlugin("io.gatling" % "sbt-plugin" % "1.0-SNAPSHOT")