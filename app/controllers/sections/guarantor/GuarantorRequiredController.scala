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

package controllers.sections.guarantor

import controllers.actions._
import forms.sections.guarantor.GuarantorRequiredFormProvider
import models.requests.DataRequest
import models.{Mode, NormalMode}
import navigation.GuarantorNavigator
import pages.sections.guarantor._
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.guarantor.{GuarantorRequiredQuestionView, GuarantorRequiredView}

import javax.inject.Inject
import scala.concurrent.Future

class GuarantorRequiredController @Inject()(
                                             override val messagesApi: MessagesApi,
                                             override val userAnswersService: UserAnswersService,
                                             override val navigator: GuarantorNavigator,
                                             override val auth: AuthAction,
                                             override val getData: DataRetrievalAction,
                                             override val requireData: DataRequiredAction,
                                             override val withMovement: MovementAction,
                                             val controllerComponents: MessagesControllerComponents,
                                             view: GuarantorRequiredView,
                                             formProvider: GuarantorRequiredFormProvider,
                                             questionView: GuarantorRequiredQuestionView
                                           ) extends GuarantorBaseController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      renderView(Ok, fillForm(GuarantorRequiredPage, formProvider()), mode)
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        renderView(BadRequest, _, mode),
        value => if (request.userAnswers.get(GuarantorRequiredPage).contains(value)) {
          Future(Redirect(navigator.nextPage(GuarantorRequiredPage, mode, request.userAnswers)))
        } else {

          val updatedUserAnswers = cleanseUserAnswersIfValueHasChanged(
            GuarantorRequiredPage,
            value,
            request.userAnswers
              .remove(GuarantorArrangerPage)
              .remove(GuarantorNamePage)
              .remove(GuarantorVatPage)
              .remove(GuarantorAddressPage)
          )

          saveAndRedirect(
            page = GuarantorRequiredPage,
            answer = value,
            currentAnswers = updatedUserAnswers,
            mode = NormalMode
          )
        }
      )
    }

  private def renderView(status: Status, form: Form[_], mode: Mode)
                        (implicit request: DataRequest[_]): Future[Result] = {
    if (GuarantorSection.requiresGuarantorToBeProvided) {
      Future(status(view(
        onwardRoute = routes.GuarantorRequiredController.enterGuarantorDetails(request.ern, request.arc),
        newGuarantorIsRequired = GuarantorSection.requiresNewGuarantorDetails
      )))
    } else {
      Future(status(questionView(
        form = form,
        onwardRoute = routes.GuarantorRequiredController.onSubmit(request.ern, request.arc, mode)
      )))
    }
  }


  def enterGuarantorDetails(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      val updatedAnswers = request.userAnswers.remove(GuarantorRequiredPage)

      userAnswersService.set(updatedAnswers).map(result => {
        Redirect(navigator.nextPage(GuarantorRequiredPage, mode, result))
      })
    }
}
