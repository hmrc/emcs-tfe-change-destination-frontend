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

package controllers.sections.transportArranger

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.transportArranger.TransportArrangerReviewFormProvider
import models.NormalMode
import models.requests.DataRequest
import navigation.TransportArrangerNavigator
import pages.sections.transportArranger.TransportArrangerReviewPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import viewmodels.checkAnswers.sections.transportArranger.TransportArrangerCheckAnswersHelper
import views.html.sections.transportArranger.TransportArrangerReviewView

import javax.inject.Inject
import scala.concurrent.Future

class TransportArrangerReviewController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val userAllowList: UserAllowListAction,
                                       override val navigator: TransportArrangerNavigator,
                                       override val auth: AuthAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       override val withMovement: MovementAction,
                                       formProvider: TransportArrangerReviewFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: TransportArrangerReviewView,
                                       helper: TransportArrangerCheckAnswersHelper
                                     ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      renderView(Ok, fillForm(TransportArrangerReviewPage, formProvider()))
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        renderView(BadRequest, _),
        value =>
          saveAndRedirect(TransportArrangerReviewPage, value, NormalMode)
      )
    }

  private def renderView(status: Status, form: Form[_])(implicit request: DataRequest[_]): Future[Result] =
    Future(status(view(form, helper.summaryList(onReviewPage = true), routes.TransportArrangerReviewController.onSubmit(request.ern, request.arc))))
}
