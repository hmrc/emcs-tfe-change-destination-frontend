/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package config

import featureswitch.core.config.{FeatureSwitching, ReturnToLegacy, WelshLanguage}
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.RequestHeader
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject()(servicesConfig: ServicesConfig, configuration: Configuration) extends FeatureSwitching {

  override lazy val config: AppConfig = this

  private lazy val host: String = configuration.get[String]("host")

  lazy val appName: String = configuration.get[String]("appName")

  lazy val selfUrl: String = servicesConfig.baseUrl("emcs-tfe-change-destination-frontend")

  lazy val timeout: Int = configuration.get[Int]("timeout-dialog.timeout")

  lazy val countdown: Int = configuration.get[Int]("timeout-dialog.countdown")

  lazy val signOutUrl: String = configuration.get[String]("urls.signOut")

  lazy val deskproName: String = configuration.get[String]("deskproName")

  lazy val loginGuidance: String = configuration.get[String]("urls.loginGuidance")

  private lazy val feedbackFrontendHost: String = configuration.get[String]("feedback-frontend.host")

  lazy val feedbackFrontendSurveyUrl: String = s"$feedbackFrontendHost/feedback/$deskproName/beta"

  private lazy val contactHost = configuration.get[String]("contact-frontend.host")

  lazy val loginUrl: String = configuration.get[String]("urls.login")

  lazy val registerGuidance: String = configuration.get[String]("urls.registerGuidance")

  lazy val signUpBetaFormUrl: String = configuration.get[String]("urls.signupBetaForm")

  def loginContinueUrl(ern: String, arc: String): String = configuration.get[String]("urls.loginContinue") + s"/trader/$ern/movement/$arc"

  def languageTranslationEnabled: Boolean = isEnabled(WelshLanguage)

  def emcsTfeService: String = servicesConfig.baseUrl("emcs-tfe")

  def emcsTfeBaseUrl: String = s"$emcsTfeService/emcs-tfe"

  def getFeatureSwitchValue(feature: String): Boolean = configuration.get[Boolean](feature)

  def emcsTfeHomeUrl(ern: Option[String]): String = {
    if (isEnabled(ReturnToLegacy)) {
      configuration.get[String]("urls.legacy.atAGlance") + ern.fold("")(s"/" + _)
    } else {
      configuration.get[String]("urls.emcsTfeHome")
    }
  }

  def emcsMovementDetailsUrl(ern: String, arc: String): String =
    if (isEnabled(ReturnToLegacy)) {
      configuration.get[String]("urls.legacy.movementHistory").replace(":ern", ern).replace(":arc", arc)
    } else {
      configuration.get[String]("urls.emcsTfeMovementDetails") + s"/$ern/$arc"
    }

  def emcsMovementsUrl(ern: String): String =
    if (isEnabled(ReturnToLegacy)) {
      configuration.get[String]("urls.legacy.movements").replace(":ern", ern)
    } else {
      configuration.get[String]("urls.emcsTfeMovementsIn") + s"/$ern"
    }

  def languageMap: Map[String, Lang] = Map(
    "en" -> Lang("en"),
    "cy" -> Lang("cy")
  )

  def betaBannerFeedbackUrl(implicit request: RequestHeader): String =
    s"$contactHost/contact/beta-feedback?service=$deskproName&backUrl=${SafeRedirectUrl(host + request.uri).encodedUrl}"

  def internalAuthToken: String = configuration.get[String]("internal-auth.token")

  private def userAllowListService: String = servicesConfig.baseUrl("user-allow-list")

  def userAllowListBaseUrl: String = s"$userAllowListService/user-allow-list"
}


