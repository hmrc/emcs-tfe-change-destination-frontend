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

import featureswitch.core.config._
import models.requests.DataRequest
import play.api.Configuration
import play.api.mvc.RequestHeader
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject()(servicesConfig: ServicesConfig, configuration: Configuration) extends FeatureSwitching {

  override val config: AppConfig = this

  lazy val host: String = configuration.get[String]("host")
  lazy val appName: String = configuration.get[String]("appName")
  lazy val deskproName: String = configuration.get[String]("deskproName")

  private lazy val contactHost = configuration.get[String]("contact-frontend.host")

  def betaBannerFeedbackUrl(implicit request: RequestHeader): String =
    s"$contactHost/contact/beta-feedback?service=$deskproName&backUrl=${SafeRedirectUrl(host + request.uri).encodedUrl}"

  lazy val loginUrl: String = configuration.get[String]("urls.login")

  def loginContinueUrl(ern: String, arc: String): String = configuration.get[String]("urls.loginContinue") + s"/trader/$ern/movement/$arc"

  def signOutUrl()(request: RequestHeader): String = {
    val savablePage = request.uri.matches(".*/trader/.*/draft/.*")

    (redirectToFeedbackSurvey, savablePage) match {
      case (true, _) => controllers.auth.routes.SignedOutController.signOutWithSurvey().url
      case (false, true) => controllers.auth.routes.SignedOutController.signOutSaved().url
      case (false, false) => controllers.auth.routes.SignedOutController.signOutNotSaved().url
    }
  }

  lazy val loginGuidance: String = configuration.get[String]("urls.loginGuidance")
  lazy val registerGuidance: String = configuration.get[String]("urls.registerGuidance")
  lazy val exciseGuidance: String = configuration.get[String]("urls.exciseGuidance")
  lazy val signUpBetaFormUrl: String = configuration.get[String]("urls.signupBetaForm")

  private lazy val feedbackFrontendHost: String = configuration.get[String]("feedback-frontend.host")
  lazy val feedbackFrontendSurveyUrl: String = s"$feedbackFrontendHost/feedback/$deskproName/beta"

  def emcsTfeHomeUrl: String =
    if (isEnabled(ReturnToLegacy)) {
      configuration.get[String]("urls.legacy.atAGlance")
    } else {
      configuration.get[String]("urls.emcsTfeHome")
    }

  def returnToDraft(implicit request: DataRequest[_]): String = controllers.routes.TaskListController.onPageLoad(request.ern, request.arc).url

  private def redirectToFeedbackSurvey: Boolean = isEnabled(RedirectToFeedbackSurvey)

  lazy val timeout: Int = configuration.get[Int]("timeout-dialog.timeout")
  lazy val countdown: Int = configuration.get[Int]("timeout-dialog.countdown")

  private def emcsTfeService: String = servicesConfig.baseUrl("emcs-tfe")

  def emcsTfeBaseUrl: String = s"$emcsTfeService/emcs-tfe"

  def emcsTfeFrontendBaseUrl: String = servicesConfig.baseUrl("emcs-tfe-frontend")

  private def traderKnownFactsReferenceDataService: String =
    if (isEnabled(StubGetTraderKnownFacts)) {
      servicesConfig.baseUrl("emcs-tfe-reference-data-stub")
    }
    else {
      servicesConfig.baseUrl("emcs-tfe-reference-data")
    }

  def traderKnownFactsReferenceDataBaseUrl: String = s"$traderKnownFactsReferenceDataService/emcs-tfe-reference-data"

  def referenceDataBaseUrl: String = servicesConfig.baseUrl("emcs-tfe-reference-data") + "/emcs-tfe-reference-data"


  def selfUrl: String = servicesConfig.baseUrl("emcs-tfe-change-destination-frontend")

  lazy val accessibilityStatementUrl: String = {
    val baseUrl = servicesConfig.getString("accessibility-statement.host")
    val servicePath = servicesConfig.getString("accessibility-statement.service-path")
    baseUrl + servicePath
  }

  def getFeatureSwitchValue(feature: String): Boolean = configuration.get[Boolean](feature)

  lazy val euCustomsOfficeCodesUrl = "https://ec.europa.eu/taxation_customs/dds2/rd/rd_home.jsp?Lang=en"
  lazy val gbCustomsOfficeCodesUrl = "https://www.gov.uk/government/publications/uk-customs-office-codes-for-data-element-512-of-the-customs-declaration-service"

  lazy val cacheTtl: Int = configuration.get[Int]("mongodb.timeToLiveInSeconds")

  def destinationOfficeSuffix: String = configuration.get[String]("constants.destinationOfficeSuffix")

  lazy val earliestDispatchDate: LocalDate = LocalDate.parse(configuration.get[String]("constants.earliestDispatchDate"))
  lazy val earliestInvoiceDate: LocalDate = LocalDate.parse(configuration.get[String]("constants.earliestInvoiceDate"))

  def betaAllowListCheckingEnabled: Boolean = isEnabled(CheckBetaAllowList)

  def betaCheckServiceName: String = configuration.get[String]("beta.serviceName")

  lazy val guarantorRequiredUrl = configuration.get[String]("urls.guarantorRequired")
}
