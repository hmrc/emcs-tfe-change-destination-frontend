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

package controllers.sections.movement

import controllers.BaseNavigationController
import controllers.actions._
import controllers.actions.predraft.{PreDraftDataRequiredAction, PreDraftDataRetrievalAction}
import forms.sections.movement.InvoiceDetailsFormProvider
import models.Mode
import models.requests.DataRequest
import models.sections.info.InvoiceDetailsModel
import navigation.MovementNavigator
import pages.QuestionPage
import pages.sections.movement.InvoiceDetailsPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc._
import services.UserAnswersService
import utils.{DateTimeUtils, TimeMachine}
import views.html.sections.movement.InvoiceDetailsView

import javax.inject.Inject
import scala.concurrent.Future

class InvoiceDetailsController @Inject()(
                                          override val messagesApi: MessagesApi,
                                          val navigator: MovementNavigator,
                                          val auth: AuthAction,
                                          val getData: DataRetrievalAction,
                                          val requireData: DataRequiredAction,
                                          val userAllowList: UserAllowListAction,
                                          val withMovement: MovementAction,
                                          formProvider: InvoiceDetailsFormProvider,
                                          val userAnswersService: UserAnswersService,
                                          val controllerComponents: MessagesControllerComponents,
                                          view: InvoiceDetailsView,
                                          timeMachine: TimeMachine
                                        ) extends BaseNavigationController with AuthActionHelper with DateTimeUtils {

  def onPreDraftPageLoad(ern: String, arc: String,  mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      renderView(
        Ok,
        InvoiceDetailsPage,
        fillForm(InvoiceDetailsPage, formProvider()),
        controllers.sections.movement.routes.InvoiceDetailsController.onPreDraftSubmit(ern, arc, mode),
        mode
      )
    }

  def onPreDraftSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          renderView(
            BadRequest,
            InvoiceDetailsPage,
            formWithErrors,
            controllers.sections.movement.routes.InvoiceDetailsController.onPreDraftSubmit(ern, arc, mode),
            mode
          ),
        value =>
          saveAndRedirect(InvoiceDetailsPage, value, mode)
      )
    }

  private def renderView(status: Status, page: QuestionPage[InvoiceDetailsModel], form: Form[_], onSubmitCall: Call, mode: Mode)
                        (implicit request: DataRequest[_]): Future[Result] = {
    Future.successful(
      status(view(
        form = form,
        currentDate = timeMachine.now().toLocalDate.formatDateNumbersOnly(),
        onSubmitCall = onSubmitCall,
        skipQuestionCall = navigator.nextPage(page, mode, request.userAnswers)
      ))
    )
  }
}
