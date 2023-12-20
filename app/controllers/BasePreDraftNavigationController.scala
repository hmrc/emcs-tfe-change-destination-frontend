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

package controllers

import models._
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario
import navigation.BaseNavigator
import pages.QuestionPage
import pages.sections.info.{DeferredMovementPage, DestinationTypePage}
import play.api.libs.json.Format
import play.api.mvc.Result
import services.PreDraftService
import utils.Logging

import scala.concurrent.Future

trait BasePreDraftNavigationController extends BaseNavigationController with Logging {

  val preDraftService: PreDraftService
  val navigator: BaseNavigator

  def savePreDraftAndRedirect[A](page: QuestionPage[A], answer: A, currentAnswers: UserAnswers, mode: Mode)
                                (implicit format: Format[A]): Future[Result] =
    savePreDraft(page, answer, currentAnswers).map { updatedAnswers =>
      Redirect(navigator.nextPage(page, mode, updatedAnswers))
    }

  def savePreDraftAndRedirect[A](page: QuestionPage[A], answer: A, mode: Mode)
                                (implicit request: DataRequest[_], format: Format[A]): Future[Result] =
    savePreDraft(page, answer).map { updatedAnswers =>
      Redirect(navigator.nextPage(page, mode, updatedAnswers))
    }

  private def savePreDraft[A](page: QuestionPage[A], answer: A, currentAnswers: UserAnswers)(implicit format: Format[A]): Future[UserAnswers] =
    if (currentAnswers.get[A](page).contains(answer)) {
      Future.successful(currentAnswers)
    } else {
      for {
        updatedAnswers <- Future.successful(currentAnswers.set(page, answer))
        _ <- preDraftService.set(updatedAnswers)
      } yield updatedAnswers
    }

  private def savePreDraft[A](page: QuestionPage[A], answer: A)
                             (implicit request: DataRequest[_], format: Format[A]): Future[UserAnswers] =
    savePreDraft(page, answer, request.userAnswers)

  protected def withDeferredMovementAnswer(isOnPreDraftFlow: Boolean)(f: Boolean => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    withAnswerAsync(
      page = DeferredMovementPage(isOnPreDraftFlow = isOnPreDraftFlow),
      redirectRoute = if (isOnPreDraftFlow) {
        controllers.sections.info.routes.DeferredMovementController.onPreDraftPageLoad(request.ern, NormalMode)
      } else {
        controllers.sections.info.routes.DeferredMovementController.onPageLoad(request.ern, request.arc)
      }
    ) {
      f(_)
    }

  protected def withDestinationTypePageAnswer(f: MovementScenario => Future[Result])(implicit request: DataRequest[_]): Future[Result] = {
    withAnswerAsync(
      page = DestinationTypePage,
      redirectRoute = controllers.sections.info.routes.DestinationTypeController.onPreDraftPageLoad(request.ern, NormalMode)
    ) {
      f(_)
    }
  }
}

