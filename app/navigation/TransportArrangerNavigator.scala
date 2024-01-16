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
import models.sections.ReviewAnswer.{ChangeAnswers, KeepAnswers}
import models.sections.transportArranger.TransportArranger.{GoodsOwner, Other}
import models.{CheckMode, Mode, NormalMode, ReviewMode, UserAnswers}
import pages.Page
import pages.sections.transportArranger._
import play.api.mvc.Call

import javax.inject.Inject

//noinspection ScalaStyle
class TransportArrangerNavigator @Inject() extends BaseNavigator {

  private def normalRoutes(implicit request: DataRequest[_]): Page => UserAnswers => Call = {

    case TransportArrangerPage => (userAnswers: UserAnswers) =>
      userAnswers.get(TransportArrangerPage) match {

        case Some(GoodsOwner) | Some(Other) =>
          controllers.sections.transportArranger.routes.TransportArrangerNameController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)

        case _ =>
          controllers.sections.transportArranger.routes.TransportArrangerCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
      }

    case TransportArrangerNamePage => (userAnswers: UserAnswers) =>
      controllers.sections.transportArranger.routes.TransportArrangerVatController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)

    case TransportArrangerVatPage => (userAnswers: UserAnswers) =>
      controllers.sections.transportArranger.routes.TransportArrangerAddressController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)

    case TransportArrangerAddressPage => (userAnswers: UserAnswers) =>
      controllers.sections.transportArranger.routes.TransportArrangerCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)

    case TransportArrangerCheckAnswersPage => (userAnswers: UserAnswers) =>
      routes.DraftMovementController.onPageLoad(userAnswers.ern, userAnswers.arc)

    case TransportArrangerReviewPage => (userAnswers: UserAnswers) =>
      userAnswers.get(TransportArrangerReviewPage) match {
        case Some(ChangeAnswers) => controllers.sections.transportArranger.routes.TransportArrangerController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        case Some(KeepAnswers) => routes.DraftMovementController.onPageLoad(userAnswers.ern, userAnswers.arc)
        case _ => controllers.sections.transportArranger.routes.TransportArrangerReviewController.onPageLoad(userAnswers.ern, userAnswers.arc)
      }

    case _ => (userAnswers: UserAnswers) =>
      controllers.sections.transportArranger.routes.TransportArrangerCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  private def checkRoutes(implicit request: DataRequest[_]): Page => UserAnswers => Call = {
    case TransportArrangerPage => (userAnswers: UserAnswers) =>
      if (
        (userAnswers.get(TransportArrangerPage).contains(GoodsOwner) || userAnswers.get(TransportArrangerPage).contains(Other)) &&
          userAnswers.get(TransportArrangerNamePage).isEmpty ||
          userAnswers.get(TransportArrangerVatPage).isEmpty ||
          userAnswers.get(TransportArrangerAddressPage).isEmpty
      ) {
        normalRoutes(request)(TransportArrangerPage)(userAnswers)
      } else {
        controllers.sections.transportArranger.routes.TransportArrangerCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
      }
    case _ => (userAnswers: UserAnswers) =>
      controllers.sections.transportArranger.routes.TransportArrangerCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  private[navigation] val reviewRouteMap: Page => UserAnswers => Call = {
    case _ =>
      (userAnswers: UserAnswers) => routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers)(implicit request: DataRequest[_]): Call = mode match {
    case NormalMode =>
      normalRoutes(request)(page)(userAnswers)
    case CheckMode =>
      checkRoutes(request)(page)(userAnswers)
    case ReviewMode =>
      reviewRouteMap(page)(userAnswers)
  }
}
