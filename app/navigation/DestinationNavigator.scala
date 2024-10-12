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

import controllers.sections.destination.routes
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.{CertifiedConsignee, TemporaryCertifiedConsignee}
import models.{CheckMode, Mode, NormalMode, ReviewMode, UserAnswers}
import pages.Page
import pages.sections.destination._
import pages.sections.info.DestinationTypePage
import play.api.mvc.Call

import javax.inject.Inject

class DestinationNavigator @Inject() extends BaseNavigator {

  private def normalRoutes(implicit request: DataRequest[_]): Page => UserAnswers => Call = {

    case DestinationWarehouseExcisePage =>
      (userAnswers: UserAnswers) => routes.DestinationConsigneeDetailsController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
    case DestinationWarehouseVatPage =>
      destinationWarehouseVatRouting()
    case DestinationDetailsChoicePage =>
      destinationDetailsChoiceRouting()
    case DestinationConsigneeDetailsPage =>
      destinationConsigneeDetailsRouting()
    case DestinationAddressPage =>
      (userAnswers: UserAnswers) => routes.DestinationCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
    case DestinationCheckAnswersPage =>
      (userAnswers: UserAnswers) => controllers.routes.TaskListController.onPageLoad(userAnswers.ern, userAnswers.arc)
    case _ =>
      (userAnswers: UserAnswers) => routes.DestinationCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  private[navigation] val checkRouteMap: Page => UserAnswers => Call = {
    case _ =>
      (userAnswers: UserAnswers) => routes.DestinationCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  private[navigation] val reviewRouteMap: Page => UserAnswers => Call = {
    case _ =>
      (userAnswers: UserAnswers) => controllers.routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers)(implicit request: DataRequest[_]): Call = mode match {
    case NormalMode =>
      normalRoutes(request)(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
    case ReviewMode =>
      reviewRouteMap(page)(userAnswers)
  }

  private def destinationWarehouseVatRouting(mode: Mode = NormalMode)(implicit request: DataRequest[_]): UserAnswers => Call = (userAnswers: UserAnswers) =>
    userAnswers.get(DestinationTypePage) match {
      case Some(CertifiedConsignee) | Some(TemporaryCertifiedConsignee) =>
        routes.DestinationConsigneeDetailsController.onPageLoad(userAnswers.ern, userAnswers.arc, mode)
      case _ =>
        routes.DestinationDetailsChoiceController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
    }

  private def destinationDetailsChoiceRouting(mode: Mode = NormalMode)(implicit request: DataRequest[_]): UserAnswers => Call = (userAnswers: UserAnswers) =>
    userAnswers.get(DestinationDetailsChoicePage) match {
      case Some(true) =>
        routes.DestinationConsigneeDetailsController.onPageLoad(userAnswers.ern, userAnswers.arc, mode)
      case Some(_) =>
        routes.DestinationCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
      case _ =>
        controllers.routes.JourneyRecoveryController.onPageLoad()
    }

  private def destinationConsigneeDetailsRouting(mode: Mode = NormalMode)(implicit request: DataRequest[_]): UserAnswers => Call = (userAnswers: UserAnswers) =>
    userAnswers.get(DestinationConsigneeDetailsPage) match {
      case Some(true) =>
        routes.DestinationCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
      case Some(false) =>
        routes.DestinationAddressController.onPageLoad(userAnswers.ern, userAnswers.arc, mode)
      case _ =>
        controllers.routes.JourneyRecoveryController.onPageLoad()
    }
}
