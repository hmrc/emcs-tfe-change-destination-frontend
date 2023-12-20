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

package controllers.sections.info

import controllers.BasePreDraftNavigationController
import controllers.actions._
import controllers.actions.predraft.{PreDraftAuthActionHelper, PreDraftDataRequiredAction, PreDraftDataRetrievalAction}
import forms.sections.info.InvoiceDetailsFormProvider
import models.requests.DataRequest
import models.sections.info.InvoiceDetailsModel
import models.{CheckMode, Mode}
import navigation.InformationNavigator
import pages.QuestionPage
import pages.sections.info.InvoiceDetailsPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc._
import services.{PreDraftService, UserAnswersService}
import utils.{DateTimeUtils, TimeMachine}
import views.html.sections.info.InvoiceDetailsView

import javax.inject.Inject
import scala.concurrent.Future

class InvoiceDetailsController @Inject()(
                                          override val messagesApi: MessagesApi,
                                          val preDraftService: PreDraftService,
                                          val navigator: InformationNavigator,
                                          val auth: AuthAction,
                                          val getPreDraftData: PreDraftDataRetrievalAction,
                                          val requirePreDraftData: PreDraftDataRequiredAction,
                                          val getData: DataRetrievalAction,
                                          val requireData: DataRequiredAction,
                                          val userAllowList: UserAllowListAction,
                                          formProvider: InvoiceDetailsFormProvider,
                                          val userAnswersService: UserAnswersService,
                                          val controllerComponents: MessagesControllerComponents,
                                          view: InvoiceDetailsView,
                                          timeMachine: TimeMachine
                                        ) extends BasePreDraftNavigationController with AuthActionHelper with PreDraftAuthActionHelper with DateTimeUtils {

  def onPreDraftPageLoad(ern: String, mode: Mode): Action[AnyContent] =
    authorisedPreDraftDataRequestAsync(ern) { implicit request =>
      renderView(
        Ok,
        InvoiceDetailsPage(isOnPreDraftFlow = true),
        fillForm(InvoiceDetailsPage(isOnPreDraftFlow = true), formProvider()),
        controllers.sections.info.routes.InvoiceDetailsController.onPreDraftSubmit(request.ern, mode),
        mode
      )
    }

  def onPreDraftSubmit(ern: String, mode: Mode): Action[AnyContent] =
    authorisedPreDraftDataRequestAsync(ern) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          renderView(
            BadRequest,
            InvoiceDetailsPage(isOnPreDraftFlow = true),
            formWithErrors,
            controllers.sections.info.routes.InvoiceDetailsController.onPreDraftSubmit(request.ern, mode),
            mode
          ),
        value =>
          savePreDraftAndRedirect(InvoiceDetailsPage(isOnPreDraftFlow = true), value, mode)
      )
    }

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      renderView(
        Ok,
        InvoiceDetailsPage(isOnPreDraftFlow = false),
        fillForm(InvoiceDetailsPage(isOnPreDraftFlow = false), formProvider()),
        controllers.sections.info.routes.InvoiceDetailsController.onSubmit(request.ern, arc),
        CheckMode
      )
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          renderView(
            BadRequest,
            InvoiceDetailsPage(isOnPreDraftFlow = false),
            formWithErrors,
            controllers.sections.info.routes.InvoiceDetailsController.onSubmit(request.ern, arc),
            CheckMode
          ),
        value =>
          saveAndRedirect(InvoiceDetailsPage(isOnPreDraftFlow = false), value, CheckMode)
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
