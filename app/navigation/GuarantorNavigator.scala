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

import controllers.sections.guarantor.routes
import models.requests.DataRequest
import models.sections.ReviewAnswer.ChangeAnswers
import models.sections.guarantor.GuarantorArranger.{GoodsOwner, Transporter}
import models.{CheckMode, Mode, NormalMode, ReviewMode, UserAnswers}
import pages.Page
import pages.sections.guarantor._
import play.api.mvc.Call

import javax.inject.Inject

//noinspection ScalaStyle
class GuarantorNavigator @Inject() extends BaseNavigator {

  private def normalRoutes(implicit request: DataRequest[_]): Page => UserAnswers => Call = {

    case GuarantorReviewPage => (userAnswers: UserAnswers) =>
      if(userAnswers.get(GuarantorRequiredPage).contains(false) && userAnswers.get(GuarantorReviewPage).contains(ChangeAnswers)) {
        routes.GuarantorArrangerController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
      } else {
        routes.GuarantorCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
      }

    case GuarantorRequiredPage => (userAnswers: UserAnswers) =>
      userAnswers.get(GuarantorRequiredPage) match {
        case Some(false) =>
          routes.GuarantorCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
        case _ =>
          routes.GuarantorArrangerController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
      }

    case GuarantorArrangerPage => (userAnswers: UserAnswers) =>
      userAnswers.get(GuarantorArrangerPage) match {
        case Some(GoodsOwner) | Some(Transporter) =>
          routes.GuarantorNameController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        case _ =>
          routes.GuarantorCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
      }

    case GuarantorNamePage => (userAnswers: UserAnswers) =>
      routes.GuarantorVatController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)

    case GuarantorVatPage => (userAnswers: UserAnswers) =>
      routes.GuarantorAddressController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)

    case GuarantorAddressPage => (userAnswers: UserAnswers) =>
      routes.GuarantorCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)

    case GuarantorCheckAnswersPage => (userAnswers: UserAnswers) =>
      controllers.routes.TaskListController.onPageLoad(userAnswers.ern, userAnswers.arc)

    case _ =>
      (userAnswers: UserAnswers) =>
        routes.GuarantorCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  private[navigation] val reviewRouteMap: Page => UserAnswers => Call = {
    case _ =>
      (userAnswers: UserAnswers) => controllers.routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  private val checkRoutes: Page => UserAnswers => Call = {
    case _ =>
      (userAnswers: UserAnswers) =>
        routes.GuarantorCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers)(implicit request: DataRequest[_]): Call = mode match {
    case NormalMode =>
      normalRoutes(request)(page)(userAnswers)
    case CheckMode =>
      checkRoutes(page)(userAnswers)
    case ReviewMode =>
      reviewRouteMap(page)(userAnswers)
  }
}
