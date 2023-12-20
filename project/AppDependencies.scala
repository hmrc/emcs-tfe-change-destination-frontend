import sbt.*

object AppDependencies {

  val playSuffix = "-play-28"
  val hmrcBootstrapVersion = "8.0.0"
  val hmrcMongoVersion = "1.5.0"

  private val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"             %%  "play-frontend-hmrc"                % s"7.29.0$playSuffix",
    "uk.gov.hmrc"             %% s"bootstrap-frontend$playSuffix"     %  hmrcBootstrapVersion,
    "uk.gov.hmrc.mongo"       %% s"hmrc-mongo$playSuffix"             %  hmrcMongoVersion,
    "uk.gov.hmrc"             %% "play-conditional-form-mapping"      % s"1.13.0$playSuffix"
  )

  private val test = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-test$playSuffix"         % hmrcBootstrapVersion,
    "uk.gov.hmrc.mongo"       %% s"hmrc-mongo-test$playSuffix"        % hmrcMongoVersion,
    "org.scalamock"           %%  "scalamock"                         % "5.2.0",
    "org.jsoup"               %   "jsoup"                             % "1.15.4"
  ).map(_ % "test, it")

  private val overrides = Seq(
    "com.google.inject" % "guice" % "5.1.0"
  )

  def apply(): Seq[ModuleID] = compile ++ test ++ overrides
}
