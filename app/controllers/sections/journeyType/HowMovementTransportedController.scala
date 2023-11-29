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

package controllers.sections.journeyType

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.journeyType.HowMovementTransportedFormProvider
import models.{Mode, NormalMode}
import navigation.JourneyTypeNavigator
import pages.sections.journeyType.{HowMovementTransportedPage, JourneyTypeSection}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.sections.journeyType.HowMovementTransportedView

import javax.inject.Inject
import scala.concurrent.Future

class HowMovementTransportedController @Inject()(
                                                  override val messagesApi: MessagesApi,
                                                  override val userAnswersService: UserAnswersService,
                                                  override val navigator: JourneyTypeNavigator,
                                                  override val auth: AuthAction,
                                                  override val getData: DataRetrievalAction,
                                                  override val requireData: DataRequiredAction,
                                                  override val withMovement: MovementAction,
                                                  formProvider: HowMovementTransportedFormProvider,
                                                  val controllerComponents: MessagesControllerComponents,
                                                  view: HowMovementTransportedView,
                                                  val userAllowList: UserAllowListAction
                                                ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovement(ern, arc) { implicit request =>
      Ok(view(fillForm(HowMovementTransportedPage, formProvider()), mode))
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),
        value => if (request.userAnswers.get(HowMovementTransportedPage).contains(value)) {
          Future(Redirect(navigator.nextPage(HowMovementTransportedPage, mode, request.userAnswers)))
        } else {

          val cleansedAnswers = request.userAnswers.remove(JourneyTypeSection)

          saveAndRedirect(
            page = HowMovementTransportedPage,
            answer = value,
            currentAnswers = cleansedAnswers,
            mode = NormalMode
          )
        }
      )
    }
}
