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
import controllers.sections.sad.{routes => sadRoutes}
import models.sections.sad.SadAddToListModel
import models.{CheckMode, Index, Mode, NormalMode, ReviewMode, UserAnswers}
import pages.Page
import pages.sections.sad._
import play.api.mvc.Call
import queries.SadCount

import javax.inject.Inject

class SadNavigator @Inject() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {

    case ImportNumberPage(_) => (userAnswers: UserAnswers) =>
      sadRoutes.SadAddToListController.onPageLoad(userAnswers.ern, userAnswers.arc)

    case SadAddToListPage => (answers: UserAnswers) =>
      answers.get(SadAddToListPage) match {
        case Some(SadAddToListModel.Yes) =>
          sadRoutes.ImportNumberController.onPageLoad(answers.ern, answers.arc, Index(answers.get(SadCount).getOrElse(0)), NormalMode)
        case Some(SadAddToListModel.NoMoreToCome) =>
          routes.DraftMovementController.onPageLoad(answers.ern, answers.arc)
        case _ =>
          controllers.routes.JourneyRecoveryController.onPageLoad()
      }

    case _ =>
      (_: UserAnswers) => testOnly.controllers.routes.UnderConstructionController.onPageLoad()
  }

  private[navigation] val checkRouteMap: Page => UserAnswers => Call = {
    case _ => (userAnswers: UserAnswers) => sadRoutes.SadAddToListController.onPageLoad(userAnswers.ern, userAnswers.arc)
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
