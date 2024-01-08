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

package controllers.sections.firstTransporter

import config.Constants.NONGBVAT
import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.firstTransporter.FirstTransporterVatFormProvider
import models.requests.DataRequest
import models.{Mode, NormalMode}
import navigation.FirstTransporterNavigator
import pages.sections.firstTransporter.FirstTransporterVatPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.firstTransporter.FirstTransporterVatView

import javax.inject.Inject
import scala.concurrent.Future

class FirstTransporterVatController @Inject()(
                                               override val messagesApi: MessagesApi,
                                               override val userAnswersService: UserAnswersService,
                                               override val navigator: FirstTransporterNavigator,
                                               override val auth: AuthAction,
                                               override val getData: DataRetrievalAction,
                                               override val requireData: DataRequiredAction,
                                               override val withMovement: MovementAction,
                                               override val userAllowList: UserAllowListAction,
                                               formProvider: FirstTransporterVatFormProvider,
                                               val controllerComponents: MessagesControllerComponents,
                                               view: FirstTransporterVatView
                                             ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      renderView(Ok, fillForm(FirstTransporterVatPage, formProvider()), mode)
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        renderView(BadRequest, _, mode),
        saveAndRedirect(FirstTransporterVatPage, _, mode)
      )
    }

  private def renderView(status: Status, form: Form[_], mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
    Future.successful(status(view(
      form,
      controllers.sections.firstTransporter.routes.FirstTransporterVatController.onSubmit(request.ern, request.arc, mode)
    )))


  def onNonGbVAT(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      saveAndRedirect(FirstTransporterVatPage, NONGBVAT, NormalMode)
    }
}
