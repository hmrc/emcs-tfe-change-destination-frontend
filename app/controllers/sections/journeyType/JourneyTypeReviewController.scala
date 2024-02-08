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

package controllers.sections.journeyType

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.journeyType.JourneyTypeReviewFormProvider
import models.NormalMode
import models.requests.DataRequest
import navigation.JourneyTypeNavigator
import pages.sections.journeyType.JourneyTypeReviewPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import viewmodels.helpers.CheckYourAnswersJourneyTypeHelper
import views.html.sections.journeyType.JourneyTypeReviewView

import javax.inject.Inject
import scala.concurrent.Future

class JourneyTypeReviewController @Inject()(
                                             override val messagesApi: MessagesApi,
                                             override val userAnswersService: UserAnswersService,
                                             override val betaAllowList: BetaAllowListAction,
                                             override val navigator: JourneyTypeNavigator,
                                             override val auth: AuthAction,
                                             override val getData: DataRetrievalAction,
                                             override val requireData: DataRequiredAction,
                                             override val withMovement: MovementAction,
                                             formProvider: JourneyTypeReviewFormProvider,
                                             val controllerComponents: MessagesControllerComponents,
                                             view: JourneyTypeReviewView,
                                             helper: CheckYourAnswersJourneyTypeHelper
                                     ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      renderView(Ok, fillForm(JourneyTypeReviewPage, formProvider()))
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        renderView(BadRequest, _),
        saveAndRedirect(JourneyTypeReviewPage, _, NormalMode)
      )
    }

  private def renderView(status: Status, form: Form[_])(implicit request: DataRequest[_]): Future[Result] =
    Future(status(view(form, helper.summaryList(onReviewPage = true), routes.JourneyTypeReviewController.onSubmit(request.ern, request.arc))))
}
