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

import controllers.actions._
import forms.ContinueDraftFormProvider
import models.UserAnswers
import pages.DeclarationPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{PreDraftService, UserAnswersService}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.ContinueDraftView

import javax.inject.Inject
import scala.concurrent.Future

class IndexController @Inject()(override val messagesApi: MessagesApi,
                                val preDraftService: PreDraftService,
                                val userAnswersService: UserAnswersService,
                                val getData: DataRetrievalAction,
                                val withMovement: MovementAction,
                                authAction: AuthAction,
                                userAllowed: UserAllowListAction,
                                val controllerComponents: MessagesControllerComponents,
                                formProvider: ContinueDraftFormProvider,
                                view: ContinueDraftView) extends BaseController {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    (authAction(ern, arc) andThen userAllowed andThen withMovement.upToDate(arc) andThen getData).async { implicit request =>
      request.userAnswers match {
        case Some(ans) if ans.getFromUserAnswersOnly(DeclarationPage).isDefined =>
          reinitialiseAndRedirect(UserAnswers(request.ern, request.arc))
        case Some(ans) if ans.data.fields.nonEmpty =>
          Future.successful(Ok(view(formProvider(), routes.IndexController.onSubmit(ern, arc))))
        case _ =>
          reinitialiseAndRedirect(UserAnswers(request.ern, request.arc))
      }
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    (authAction(ern, arc) andThen userAllowed andThen withMovement.upToDate(arc) andThen getData).async { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, routes.IndexController.onSubmit(ern, arc)))),
        continueDraft => {
          if (continueDraft) {
            Future(Redirect(controllers.routes.DraftMovementController.onPageLoad(ern, arc)))
          } else {
            reinitialiseAndRedirect(UserAnswers(request.ern, request.arc))
          }
        }
      )
    }

  private def reinitialiseAndRedirect(answers: UserAnswers)(implicit hc: HeaderCarrier): Future[Result] =
    preDraftService.set(answers).flatMap { _ =>
      userAnswersService.set(answers).map { _ =>
        Redirect(controllers.sections.info.routes.InfoIndexController.onPageLoad(answers.ern, answers.arc))
      }
    }

}
