import sbt._

object AppDependencies {

  import play.core.PlayVersion

  val playSuffix = "-play-28"
  val scalatestVersion = "3.2.15"
  val hmrcBootstrapVersion = "7.21.0"
  val hmrcMongoVersion = "1.3.0"

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"             %%  "play-frontend-hmrc"                % s"7.18.0$playSuffix",
    "uk.gov.hmrc"             %% s"bootstrap-frontend$playSuffix"     %  hmrcBootstrapVersion,
    "uk.gov.hmrc.mongo"       %% s"hmrc-mongo$playSuffix"             %  hmrcMongoVersion,
    "com.google.inject"       %   "guice"                             % "5.1.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-test$playSuffix"         % hmrcBootstrapVersion,
    "org.scalatest"           %%  "scalatest"                         % scalatestVersion,
    "org.scalatestplus"       %%  "scalacheck-1-17"                   % s"$scalatestVersion.0",
    "org.scalatestplus"       %%  "mockito-4-6"                       % s"$scalatestVersion.0",
    "org.scalatestplus.play"  %%  "scalatestplus-play"                % "5.1.0",
    "org.scalamock"           %%  "scalamock"                         % "5.2.0",
    "org.pegdown"             %   "pegdown"                           % "1.6.0",
    "org.jsoup"               %   "jsoup"                             % "1.15.4",
    "com.typesafe.play"       %%  "play-test"                         % PlayVersion.current,
    "uk.gov.hmrc.mongo"       %% s"hmrc-mongo-test$playSuffix"        % hmrcMongoVersion,
    "com.vladsch.flexmark"    %   "flexmark-all"                      % "0.62.2"
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
