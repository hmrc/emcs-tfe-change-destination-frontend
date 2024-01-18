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
import models.{CheckMode, Mode, NormalMode, ReviewMode, UserAnswers}
import pages._
import pages.sections.firstTransporter.{FirstTransporterAddressPage, FirstTransporterCheckAnswersPage, FirstTransporterNamePage, FirstTransporterReviewPage, FirstTransporterVatPage}
import play.api.mvc.Call

import javax.inject.Inject

//noinspection ScalaStyle
class FirstTransporterNavigator @Inject() extends BaseNavigator {

  private def normalRoutes(implicit request: DataRequest[_]): Page => UserAnswers => Call = {

    case FirstTransporterNamePage => (userAnswers: UserAnswers) =>
      controllers.sections.firstTransporter.routes.FirstTransporterVatController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)

    case FirstTransporterVatPage => (userAnswers: UserAnswers) =>
      controllers.sections.firstTransporter.routes.FirstTransporterAddressController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)

    case FirstTransporterAddressPage => (userAnswers: UserAnswers) =>
      controllers.sections.firstTransporter.routes.FirstTransporterCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)

    case FirstTransporterCheckAnswersPage => (userAnswers: UserAnswers) =>
      routes.TaskListController.onPageLoad(userAnswers.ern, userAnswers.arc)

    case FirstTransporterReviewPage => (userAnswers: UserAnswers) =>
      userAnswers.get(FirstTransporterReviewPage) match {
        case Some(ChangeAnswers) => controllers.sections.firstTransporter.routes.FirstTransporterNameController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        case Some(KeepAnswers) => routes.TaskListController.onPageLoad(userAnswers.ern, userAnswers.arc)
        case _ => controllers.sections.firstTransporter.routes.FirstTransporterReviewController.onPageLoad(userAnswers.ern, userAnswers.arc)
      }

    case _ => (userAnswers: UserAnswers) =>
      controllers.sections.firstTransporter.routes.FirstTransporterCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  private def checkRoutes(implicit request: DataRequest[_]): Page => UserAnswers => Call = {
    case FirstTransporterNamePage => (userAnswers: UserAnswers) =>
      if (
        userAnswers.get(FirstTransporterNamePage).isEmpty ||
          userAnswers.get(FirstTransporterVatPage).isEmpty ||
          userAnswers.get(FirstTransporterAddressPage).isEmpty
      ) {
        normalRoutes(request)(FirstTransporterNamePage)(userAnswers)
      } else {
        controllers.sections.firstTransporter.routes.FirstTransporterCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
      }
    case _ => (userAnswers: UserAnswers) =>
      controllers.sections.firstTransporter.routes.FirstTransporterCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
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
