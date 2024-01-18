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

package controllers.sections.movement

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.movement.MovementReviewAnswersFormProvider
import models.NormalMode
import models.requests.DataRequest
import navigation.MovementNavigator
import pages.sections.movement.MovementReviewPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import utils.DateTimeUtils
import viewmodels.checkAnswers.sections.movement.MovementReviewAnswersHelper
import views.html.sections.movement.MovementReviewAnswersView

import javax.inject.Inject
import scala.concurrent.Future

class MovementReviewAnswersController @Inject()(
                                                 override val messagesApi: MessagesApi,
                                                 override val userAnswersService: UserAnswersService,
                                                 override val userAllowList: UserAllowListAction,
                                                 override val navigator: MovementNavigator,
                                                 override val auth: AuthAction,
                                                 override val getData: DataRetrievalAction,
                                                 override val requireData: DataRequiredAction,
                                                 override val withMovement: MovementAction,
                                                 formProvider: MovementReviewAnswersFormProvider,
                                                 val controllerComponents: MessagesControllerComponents,
                                                 view: MovementReviewAnswersView,
                                                 movementReviewAnswersHelper: MovementReviewAnswersHelper
                                               ) extends BaseNavigationController with AuthActionHelper with DateTimeUtils {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      renderView(Ok, fillForm(MovementReviewPage, formProvider()))
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        renderView(BadRequest, _),
        saveAndRedirect(MovementReviewPage, _, NormalMode)
      )
    }

  private def renderView(status: Status, form: Form[_])(implicit request: DataRequest[_]): Future[Result] =
    Future(status(view(
      form = form,
      list = movementReviewAnswersHelper.summaryList(),
      onSubmitCall = controllers.sections.movement.routes.MovementReviewAnswersController.onSubmit(request.ern, request.arc)
    )))
}
