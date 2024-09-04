/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.sections.guarantor

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.guarantor.GuarantorReviewFormProvider
import models.NormalMode
import models.requests.DataRequest
import models.response.emcsTfe.GuarantorType
import models.sections.ReviewAnswer
import models.sections.ReviewAnswer.ChangeAnswers
import navigation.GuarantorNavigator
import pages.sections.guarantor.{GuarantorRequiredPage, GuarantorReviewPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import viewmodels.checkAnswers.sections.guarantor.GuarantorCheckAnswersHelper
import views.html.sections.guarantor.GuarantorReviewView

import javax.inject.Inject
import scala.concurrent.Future

class GuarantorReviewController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           override val userAnswersService: UserAnswersService,
                                           override val navigator: GuarantorNavigator,
                                           override val auth: AuthAction,
                                           override val getData: DataRetrievalAction,
                                           override val requireData: DataRequiredAction,
                                           override val withMovement: MovementAction,
                                           formProvider: GuarantorReviewFormProvider,
                                           val controllerComponents: MessagesControllerComponents,
                                           view: GuarantorReviewView,
                                           helper: GuarantorCheckAnswersHelper
                                         ) extends BaseNavigationController with AuthActionHelper {


  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      renderView(Ok, formProvider())
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        renderView(BadRequest, _),
        redirect
      )
    }

  private def renderView(status: Status, form: Form[_])(implicit request: DataRequest[_]): Future[Result] =
    Future(status(view(form, helper.summaryList(onReviewPage = true), routes.GuarantorReviewController.onSubmit(request.ern, request.arc))))

  private def redirect(answer: ReviewAnswer)(implicit request: DataRequest[_]): Future[Result] = {
    val updatedAnswers =
      if(GuarantorType.noGuarantorValues.contains(request.movementDetails.movementGuarantee.guarantorTypeCode) && answer == ChangeAnswers) {
        request.userAnswers
          .set(GuarantorReviewPage, answer).set(GuarantorRequiredPage, true)
      } else {
        request.userAnswers.set(GuarantorReviewPage, answer)
      }

    userAnswersService.set(updatedAnswers) map { _ =>
      Redirect(navigator.nextPage(GuarantorReviewPage, NormalMode, updatedAnswers))
    }
  }
}
