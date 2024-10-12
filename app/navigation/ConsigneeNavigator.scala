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

package navigation

import controllers.routes
import models.requests.DataRequest
import models.sections.consignee.ConsigneeExportInformation.{EoriNumber, NoInformation, VatNumber}
import models.{CheckMode, Mode, NormalMode, ReviewMode, UserAnswers}
import pages.Page
import pages.sections.consignee._
import play.api.mvc.Call
import utils.Logging

import javax.inject.Inject

//noinspection ScalaStyle
class ConsigneeNavigator @Inject() extends BaseNavigator with Logging {

  private def normalRoutes(implicit request: DataRequest[_]): Page => UserAnswers => Call = {

    case ConsigneeExemptOrganisationPage => (userAnswers: UserAnswers) =>
      controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)

    case ConsigneeExportInformationPage => (userAnswers: UserAnswers) =>
      userAnswers.get(ConsigneeExportInformationPage) match {
        case Some(answers) if answers.contains(VatNumber) =>
          controllers.sections.consignee.routes.ConsigneeExportVatController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        case Some(answers) if answers.contains(EoriNumber) =>
          controllers.sections.consignee.routes.ConsigneeExportEoriController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        case Some(answers) if answers.contains(NoInformation) =>
          controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        case _ =>
          logger.warn("[normalRoutes] - Unexpected answer for ConsigneeExportInformationPage")
          controllers.routes.JourneyRecoveryController.onPageLoad()
      }

    case ConsigneeExportVatPage => (userAnswers: UserAnswers) =>
      userAnswers.get(ConsigneeExportInformationPage) match {
        case Some(answers) if answers.contains(EoriNumber) =>
          controllers.sections.consignee.routes.ConsigneeExportEoriController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        case _ =>
          controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
      }

    case ConsigneeExportEoriPage => (userAnswers: UserAnswers) =>
      controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)

    case ConsigneeExcisePage => (userAnswers: UserAnswers) =>
      controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)

    case ConsigneeAddressPage =>
      (userAnswers: UserAnswers) => controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onPageLoad(userAnswers.ern, userAnswers.arc)

    case CheckAnswersConsigneePage =>
      (userAnswers: UserAnswers) => routes.TaskListController.onPageLoad(userAnswers.ern, userAnswers.arc)

    case _ =>
      (userAnswers: UserAnswers) => controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }


  private[navigation] val checkRouteMap: Page => UserAnswers => Call = {
    case _ =>
      (userAnswers: UserAnswers) => controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  private[navigation] val reviewRouteMap: Page => UserAnswers => Call = {
    case _ =>
      (userAnswers: UserAnswers) => routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers)(implicit request: DataRequest[_]): Call = mode match {
    case NormalMode =>
      normalRoutes(request)(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
    case ReviewMode =>
      reviewRouteMap(page)(userAnswers)
  }
}
