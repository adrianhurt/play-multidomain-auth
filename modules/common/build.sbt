Common.moduleSettings("common")

// Add here the specific settings for this module


libraryDependencies ++= Common.commonDependencies ++: Seq(
	"com.typesafe.play.plugins" %% "play-plugins-mailer" % "2.3.0"
	// Add here the specific dependencies for this module:
	// jdbc,
	// anorm
)