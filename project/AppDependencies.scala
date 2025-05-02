import sbt.*

object AppDependencies {

  val playSuffix = "-play-30"
  val hmrcBootstrapVersion = "9.11.0"
  val hmrcMongoVersion = "2.6.0"
  val catsCoreVersion   =  "2.13.0"

  private val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"             %% s"play-frontend-hmrc$playSuffix"               % "11.13.0",
    "uk.gov.hmrc"             %% s"bootstrap-frontend$playSuffix"               %   hmrcBootstrapVersion,
    "uk.gov.hmrc.mongo"       %% s"hmrc-mongo$playSuffix"                       %   hmrcMongoVersion,
    "uk.gov.hmrc"             %% s"play-conditional-form-mapping$playSuffix"    % "3.3.0",
    "org.typelevel"           %%  "cats-core"                                   % catsCoreVersion
  )

  private val test = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-test$playSuffix"                   %  hmrcBootstrapVersion,
    "uk.gov.hmrc.mongo"       %% s"hmrc-mongo-test$playSuffix"                  %  hmrcMongoVersion,
    "org.scalamock"           %%  "scalamock"                                   % "5.2.0",
    "org.jsoup"               %   "jsoup"                                       % "1.18.1"
  ).map(_ % Test)

  private val overrides = Seq(
    "com.google.inject" % "guice" % "5.1.0"
  )

  val it: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% s"bootstrap-test$playSuffix" % hmrcBootstrapVersion % Test
  )

  def apply(): Seq[ModuleID] = compile ++ test ++ overrides
}
