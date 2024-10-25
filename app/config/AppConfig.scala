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
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject()(servicesConfig: ServicesConfig, configuration: Configuration) extends FeatureSwitching {

  override val config: AppConfig = this

  lazy val host: String = configuration.get[String]("host")
  lazy val appName: String = configuration.get[String]("appName")
  lazy val deskproName: String = configuration.get[String]("deskproName")

  lazy val loginUrl: String = configuration.get[String]("urls.login")

  def loginContinueUrl(ern: String, arc: String): String = configuration.get[String]("urls.loginContinue") + s"/trader/$ern/movement/$arc"

  lazy val signOutUrl = configuration.get[String]("urls.signOut")

  lazy val loginGuidance: String = configuration.get[String]("urls.loginGuidance")
  lazy val registerGuidance: String = configuration.get[String]("urls.registerGuidance")
  lazy val exciseGuidance: String = configuration.get[String]("urls.exciseGuidance")
  lazy val signUpBetaFormUrl: String = configuration.get[String]("urls.signupBetaForm")

  private lazy val feedbackFrontendHost: String = configuration.get[String]("feedback-frontend.host")
  lazy val feedbackFrontendSurveyUrl: String = s"$feedbackFrontendHost/feedback/$deskproName"

  def emcsTfeHomeUrl: String =
    configuration.get[String]("urls.emcsTfeHome")

  def emcsMovementDetailsUrl(ern: String, arc: String): String =
    configuration.get[String]("urls.emcsTfeMovementDetails").replace(":ern", ern).replace(":arc", arc)

  def emcsMovementsUrl(ern: String): String =
    configuration.get[String]("urls.emcsTfeMovementsIn").replace(":ern", ern)

  def returnToDraft(implicit request: DataRequest[_]): String = controllers.routes.TaskListController.onPageLoad(request.ern, request.arc).url

  lazy val timeout: Int = configuration.get[Int]("timeout-dialog.timeout")
  lazy val countdown: Int = configuration.get[Int]("timeout-dialog.countdown")

  private def emcsTfeService: String = servicesConfig.baseUrl("emcs-tfe")

  def emcsTfeBaseUrl: String = s"$emcsTfeService/emcs-tfe"

  def emcsTfeFrontendBaseUrl: String = servicesConfig.baseUrl("emcs-tfe-frontend")

  def traderKnownFactsBaseUrl: String =
    emcsTfeService + "/emcs-tfe/trader-known-facts"

  def referenceDataBaseUrl: String = servicesConfig.baseUrl("emcs-tfe-reference-data") + "/emcs-tfe-reference-data"

  private def nrsBrokerService: String = servicesConfig.baseUrl("nrs-broker")

  def nrsBrokerBaseUrl(): String = s"$nrsBrokerService/emcs-tfe-nrs-message-broker"

  def selfUrl: String = servicesConfig.baseUrl("emcs-tfe-change-destination-frontend")

  def getFeatureSwitchValue(feature: String): Boolean = configuration.get[Boolean](feature)

  lazy val euCustomsOfficeCodesUrl = "https://ec.europa.eu/taxation_customs/dds2/rd/rd_home.jsp?Lang=en"
  lazy val gbCustomsOfficeCodesUrl = "https://www.gov.uk/government/publications/uk-customs-office-codes-for-data-element-512-of-the-customs-declaration-service"

  lazy val cacheTtl: Int = configuration.get[Int]("mongodb.timeToLiveInSeconds")

  def destinationOfficeSuffix: String = configuration.get[String]("constants.destinationOfficeSuffix")

  lazy val earliestDispatchDate: LocalDate = LocalDate.parse(configuration.get[String]("constants.earliestDispatchDate"))
  lazy val earliestInvoiceDate: LocalDate = LocalDate.parse(configuration.get[String]("constants.earliestInvoiceDate"))

  lazy val guarantorRequiredUrl = configuration.get[String]("urls.guarantorRequired")
}
