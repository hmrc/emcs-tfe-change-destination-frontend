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
import forms.sections.destination.DestinationBusinessNameFormProvider
import models.Mode
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario
import navigation.DestinationNavigator
import pages.sections.destination.DestinationBusinessNamePage
import pages.sections.info.DestinationTypePage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.destination.DestinationBusinessNameView

import javax.inject.Inject
import scala.concurrent.Future

class DestinationBusinessNameController @Inject()(override val messagesApi: MessagesApi,
                                                  override val userAnswersService: UserAnswersService,
                                                  override val navigator: DestinationNavigator,
                                                  override val auth: AuthAction,
                                                  override val getData: DataRetrievalAction,
                                                  override val requireData: DataRequiredAction,
                                                  override val withMovement: MovementAction,
                                                  override val userAllowList: UserAllowListAction,
                                                  formProvider: DestinationBusinessNameFormProvider,
                                                  val controllerComponents: MessagesControllerComponents,
                                                  view: DestinationBusinessNameView
                                                 ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovement(ern, arc) { implicit request =>
      withAnswer(DestinationTypePage, controllers.sections.destination.routes.DestinationIndexController.onPageLoad(ern, arc)) {
        destinationType =>
          renderView(Ok, fillForm(DestinationBusinessNamePage, formProvider()), mode, destinationType)
      }
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      withAnswerAsync(DestinationTypePage, controllers.sections.destination.routes.DestinationIndexController.onPageLoad(ern, arc)) {
        destinationType =>
          formProvider().bindFromRequest().fold(
            formWithErrors =>
              Future(renderView(BadRequest, formWithErrors, mode, destinationType)),
            value =>
              saveAndRedirect(DestinationBusinessNamePage, value, mode)
          )
      }
    }

  def renderView(status: Status, form: Form[_], mode: Mode, destinationType: MovementScenario)(implicit request: DataRequest[_]): Result =
    status(view(
      form = form,
      onSubmitCall = controllers.sections.destination.routes.DestinationBusinessNameController.onSubmit(request.ern, request.arc, mode),
      destinationType = destinationType,
      QuestionSkipCall = routes.DestinationBusinessNameController.skipThisQuestion(request.ern, request.arc, mode)
    ))

  def skipThisQuestion(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      val newUserAnswers = request.userAnswers.remove(DestinationBusinessNamePage)
      userAnswersService.set(newUserAnswers).map(result => {
        Redirect(navigator.nextPage(DestinationBusinessNamePage, mode, result))
      }
      )
    }

}
