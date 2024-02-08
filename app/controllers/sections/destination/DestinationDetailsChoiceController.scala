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
import forms.sections.destination.DestinationDetailsChoiceFormProvider
import models.{Mode, NormalMode}
import navigation.DestinationNavigator
import pages.sections.destination.{DestinationAddressPage, DestinationBusinessNamePage, DestinationConsigneeDetailsPage, DestinationDetailsChoicePage}
import pages.sections.info.DestinationTypePage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.sections.destination.DestinationDetailsChoiceView

import javax.inject.Inject
import scala.concurrent.Future

class DestinationDetailsChoiceController @Inject()(override val messagesApi: MessagesApi,
                                                   override val userAnswersService: UserAnswersService,
                                                   override val navigator: DestinationNavigator,
                                                   override val auth: AuthAction,
                                                   override val getData: DataRetrievalAction,
                                                   override val requireData: DataRequiredAction,
                                                   override val withMovement: MovementAction,
                                                   formProvider: DestinationDetailsChoiceFormProvider,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   view: DestinationDetailsChoiceView,
                                                   val betaAllowList: BetaAllowListAction
                                                  ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovement(ern, arc) {
      implicit request =>
        withAnswer(DestinationTypePage) {
          movementScenario =>
            Ok(view(
              form = fillForm(DestinationDetailsChoicePage, formProvider(movementScenario)),
              action = routes.DestinationDetailsChoiceController.onSubmit(ern, arc, mode),
              movementScenario = movementScenario
            ))
        }
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) {
      implicit request =>
        withAnswerAsync(DestinationTypePage) {
          movementScenario =>
            formProvider(movementScenario).bindFromRequest().fold(
              formWithErrors => Future.successful(BadRequest(view(
                form = formWithErrors,
                action = routes.DestinationDetailsChoiceController.onSubmit(ern, arc, mode),
                movementScenario = movementScenario
              ))),
              value =>
                if (request.userAnswers.get(DestinationDetailsChoicePage).contains(value)) {
                  Future(Redirect(navigator.nextPage(DestinationDetailsChoicePage, mode, request.userAnswers)))
                } else {

                  val cleanedUserAnswers = if (value) request.userAnswers else request.userAnswers
                    .remove(DestinationConsigneeDetailsPage)
                    .remove(DestinationBusinessNamePage)
                    .remove(DestinationAddressPage)

                  saveAndRedirect(
                    page = DestinationDetailsChoicePage,
                    answer = value,
                    currentAnswers = cleanedUserAnswers,
                    mode = NormalMode
                  )
                }
            )
        }
    }
}
