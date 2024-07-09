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

package controllers.sections.destination

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.destination.DestinationWarehouseExciseFormProvider
import models.Mode
import models.requests.DataRequest
import navigation.DestinationNavigator
import pages.sections.destination.DestinationWarehouseExcisePage
import pages.sections.info.DestinationTypePage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.destination.DestinationWarehouseExciseView

import javax.inject.Inject
import scala.concurrent.Future

class DestinationWarehouseExciseController @Inject()(
                                                      override val messagesApi: MessagesApi,
                                                      override val userAnswersService: UserAnswersService,
                                                      override val navigator: DestinationNavigator,
                                                      override val auth: AuthAction,
                                                      override val getData: DataRetrievalAction,
                                                      override val requireData: DataRequiredAction,
                                                      override val withMovement: MovementAction,
                                                      override val betaAllowList: BetaAllowListAction,
                                                      formProvider: DestinationWarehouseExciseFormProvider,
                                                      val controllerComponents: MessagesControllerComponents,
                                                      view: DestinationWarehouseExciseView
                                                    ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovement(ern, arc) { implicit request =>
      withAnswer(DestinationTypePage) { movementScenario =>
        renderView(Ok, fillForm(DestinationWarehouseExcisePage, formProvider(movementScenario)), mode)
      }
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      withAnswerAsync(DestinationTypePage) { movementScenario =>
        formProvider(movementScenario).bindFromRequest().fold(
          formWithError => Future.successful(renderView(BadRequest, formWithError, mode)),
          saveAndRedirect(DestinationWarehouseExcisePage, _, mode)
        )
      }
    }

  private def renderView(status: Status, form: Form[_], mode: Mode)(implicit request: DataRequest[_]): Result = {
    status(view(
      form,
      onSubmitCall = controllers.sections.destination.routes.DestinationWarehouseExciseController.onSubmit(request.ern, request.arc, mode)
    ))
  }

}
