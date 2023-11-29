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
import controllers.sections.transportUnit.{routes => transportUnitRoutes}
import models.requests.DataRequest
import models.sections.transportUnit.TransportUnitsAddToListModel
import models.{CheckMode, Index, Mode, NormalMode, ReviewMode, UserAnswers}
import pages.Page
import pages.sections.transportUnit._
import play.api.mvc.Call
import queries.TransportUnitsCount

import javax.inject.Inject

//noinspection ScalaStyle
class TransportUnitNavigator @Inject() extends BaseNavigator {

  private def normalRoutes(implicit request: DataRequest[_]): Page => UserAnswers => Call = {
    case TransportUnitTypePage(idx) => (userAnswers: UserAnswers) =>
      transportUnitRoutes.TransportUnitIdentityController.onPageLoad(userAnswers.ern, userAnswers.arc, idx, NormalMode)
    case TransportUnitIdentityPage(idx) => (userAnswers: UserAnswers) =>
      transportUnitRoutes.TransportSealChoiceController.onPageLoad(userAnswers.ern, userAnswers.arc, idx, NormalMode)

    case TransportSealChoicePage(idx) => (userAnswers: UserAnswers) =>
      userAnswers.get(TransportSealChoicePage(idx)) match {
        case Some(true) =>
          transportUnitRoutes.TransportSealTypeController.onPageLoad(userAnswers.ern, userAnswers.arc, idx, NormalMode)
        case _ =>
          transportUnitRoutes.TransportUnitGiveMoreInformationChoiceController.onPageLoad(userAnswers.ern, userAnswers.arc, idx, NormalMode)
      }

    case TransportSealTypePage(idx) => (userAnswers: UserAnswers) =>
      transportUnitRoutes.TransportUnitGiveMoreInformationChoiceController.onPageLoad(userAnswers.ern, userAnswers.arc, idx, NormalMode)

    case TransportUnitGiveMoreInformationChoicePage(idx) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(TransportUnitGiveMoreInformationChoicePage(idx)) match {
          case Some(true) =>
            controllers.sections.transportUnit.routes.TransportUnitGiveMoreInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, idx, NormalMode)
          case _ =>
            transportUnitRoutes.TransportUnitsAddToListController.onPageLoad(userAnswers.ern, userAnswers.arc)
        }

    case TransportUnitGiveMoreInformationPage(_) => (userAnswers: UserAnswers) =>
      transportUnitRoutes.TransportUnitsAddToListController.onPageLoad(userAnswers.ern, userAnswers.arc)

    case TransportUnitsAddToListPage => (answers: UserAnswers) =>
      answers.get(TransportUnitsAddToListPage) match {
        case Some(TransportUnitsAddToListModel.Yes) =>
          transportUnitRoutes.TransportUnitTypeController.onPageLoad(answers.ern, answers.arc, Index(answers.get(TransportUnitsCount).getOrElse(0)), NormalMode)
        case Some(TransportUnitsAddToListModel.NoMoreToCome | TransportUnitsAddToListModel.MoreToCome) =>
          routes.DraftMovementController.onPageLoad(answers.ern, answers.arc)
        case _ =>
          controllers.routes.JourneyRecoveryController.onPageLoad()
      }

    case _ =>
      (userAnswers: UserAnswers) => transportUnitRoutes.TransportUnitsAddToListController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  private[navigation] def checkRouteMap(implicit request: DataRequest[_]): Page => UserAnswers => Call = {
    case TransportSealChoicePage(idx) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(TransportSealChoicePage(idx)) match {
          case Some(true) => transportUnitRoutes.TransportSealTypeController.onPageLoad(userAnswers.ern, userAnswers.arc, idx, CheckMode)
          case _ => transportUnitRoutes.TransportUnitsAddToListController.onPageLoad(userAnswers.ern, userAnswers.arc)
        }

    case _ =>
      (userAnswers: UserAnswers) => transportUnitRoutes.TransportUnitsAddToListController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  private[navigation] val reviewRouteMap: Page => UserAnswers => Call = {
    _ =>
      (userAnswers: UserAnswers) => controllers.routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers)(implicit request: DataRequest[_]): Call = mode match {
    case NormalMode =>
      normalRoutes(request)(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(request)(page)(userAnswers)
    case ReviewMode =>
      reviewRouteMap(page)(userAnswers)
  }
}
