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
import models._
import pages._
import pages.sections.info._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class InformationNavigator @Inject()() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {

    case DispatchPlacePage =>
      (userAnswers: UserAnswers) => controllers.sections.info.routes.DestinationTypeController.onPreDraftPageLoad(userAnswers.ern, NormalMode)

    case DestinationTypePage =>
      (userAnswers: UserAnswers) => controllers.sections.info.routes.DeferredMovementController.onPreDraftPageLoad(userAnswers.ern, NormalMode)

    case DeferredMovementPage(_) =>
      (userAnswers: UserAnswers) => controllers.sections.info.routes.LocalReferenceNumberController.onPreDraftPageLoad(userAnswers.ern, NormalMode)

    case LocalReferenceNumberPage(_) =>
      (userAnswers: UserAnswers) => controllers.sections.info.routes.InvoiceDetailsController.onPreDraftPageLoad(userAnswers.ern, NormalMode)

    case InvoiceDetailsPage(_) =>
      (userAnswers: UserAnswers) => controllers.sections.info.routes.DispatchDetailsController.onPreDraftPageLoad(userAnswers.ern, NormalMode)

    case DispatchDetailsPage(_) =>
      (userAnswers: UserAnswers) => controllers.sections.info.routes.InformationCheckAnswersController.onPreDraftPageLoad(userAnswers.ern)

    case InformationCheckAnswersPage =>
      (userAnswers: UserAnswers) => routes.DraftMovementController.onPageLoad(userAnswers.ern, userAnswers.arc)

    case _ =>
      (userAnswers: UserAnswers) => controllers.routes.IndexController.onPageLoad(userAnswers.ern)
  }

  private[navigation] val checkRouteMap: Page => UserAnswers => Call = {
    case DeferredMovementPage(false) =>
      (userAnswers: UserAnswers) => controllers.sections.info.routes.InformationCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
    case LocalReferenceNumberPage(false) =>
      (userAnswers: UserAnswers) => controllers.sections.info.routes.InformationCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
    case InvoiceDetailsPage(false) =>
      (userAnswers: UserAnswers) => controllers.sections.info.routes.InformationCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
    case DispatchDetailsPage(false) =>
      (userAnswers: UserAnswers) => controllers.sections.info.routes.InformationCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
    case _ =>
      (userAnswers: UserAnswers) => controllers.sections.info.routes.InformationCheckAnswersController.onPreDraftPageLoad(userAnswers.ern)
  }

  private[navigation] val reviewRouteMap: Page => UserAnswers => Call = {
    case _ =>
      (userAnswers: UserAnswers) => routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
    case ReviewMode =>
      reviewRouteMap(page)(userAnswers)
  }
}
