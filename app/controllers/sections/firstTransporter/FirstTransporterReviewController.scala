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

package controllers.sections.firstTransporter

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.firstTransporter.FirstTransporterReviewFormProvider
import models.NormalMode
import models.requests.DataRequest
import navigation.FirstTransporterNavigator
import pages.sections.firstTransporter.FirstTransporterReviewPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import viewmodels.checkAnswers.sections.firstTransporter.FirstTransporterCheckAnswersHelper
import views.html.sections.firstTransporter.FirstTransporterReviewView

import javax.inject.Inject
import scala.concurrent.Future

class FirstTransporterReviewController @Inject()(
                                                  override val messagesApi: MessagesApi,
                                                  override val userAnswersService: UserAnswersService,
                                                  override val betaAllowList: BetaAllowListAction,
                                                  override val navigator: FirstTransporterNavigator,
                                                  override val auth: AuthAction,
                                                  override val getData: DataRetrievalAction,
                                                  override val requireData: DataRequiredAction,
                                                  override val withMovement: MovementAction,
                                                  formProvider: FirstTransporterReviewFormProvider,
                                                  val controllerComponents: MessagesControllerComponents,
                                                  view: FirstTransporterReviewView,
                                                  helper: FirstTransporterCheckAnswersHelper
                                                ) extends BaseNavigationController with AuthActionHelper {


  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      renderView(Ok, fillForm(FirstTransporterReviewPage, formProvider()))
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        renderView(BadRequest, _),
        saveAndRedirect(FirstTransporterReviewPage, _, NormalMode)
      )
    }

  private def renderView(status: Status, form: Form[_])(implicit request: DataRequest[_]): Future[Result] =
    Future(status(view(form, helper.summaryList(onReviewPage = true), routes.FirstTransporterReviewController.onSubmit(request.ern, request.arc))))
}
