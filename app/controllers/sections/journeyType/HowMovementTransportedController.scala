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
import models.requests.DataRequest
import models.sections.journeyType.HowMovementTransported
import models.sections.journeyType.HowMovementTransported.FixedTransportInstallations
import models.sections.transportUnit.TransportUnitType.FixedTransport
import models.{Index, Mode, NormalMode, UserAnswers}
import navigation.JourneyTypeNavigator
import pages.sections.journeyType.{HowMovementTransportedPage, JourneyTypeSection}
import pages.sections.transportUnit.{TransportUnitTypePage, TransportUnitsSection}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.sections.journeyType.{HowMovementTransportedView, HowMovementTransportedNoOptionView}

import javax.inject.Inject
import scala.concurrent.Future
import pages.sections.info.DestinationTypePage
import pages.sections.guarantor.GuarantorRequiredPage
import models.sections.info.movementScenario.MovementType
import play.api.mvc.Result

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
                                                  onlyFixedView: HowMovementTransportedNoOptionView,
                                                  val userAllowList: UserAllowListAction
                                                ) extends BaseNavigationController with AuthActionHelper {

  private def guarantorNotRequiredEuGuard[T](onEuNotRequired: => T, default: => T)(implicit request: DataRequest[_]): T = {
    (request.userAnswers.get(DestinationTypePage), request.userAnswers.get(GuarantorRequiredPage)) match {
      case (Some(scenario), Some(false)) if scenario.movementType == MovementType.UkToEu => onEuNotRequired
      case _ => default
    }
  }
  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovement(ern, arc) { implicit request =>
      guarantorNotRequiredEuGuard(
        onEuNotRequired = Ok(onlyFixedView(mode)),
        default = Ok(view(fillForm(HowMovementTransportedPage, formProvider()), mode))
      )
    }

  private def redirect(answer: HowMovementTransported, mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
    if (request.userAnswers.get(HowMovementTransportedPage).contains(answer)) {
      Future(Redirect(navigator.nextPage(HowMovementTransportedPage, mode, request.userAnswers)))
    } else {
      val newUserAnswers = cleanseAnswers(answer)
      saveAndRedirect(
        page = HowMovementTransportedPage,
        answer = answer,
        currentAnswers = newUserAnswers,
        mode = NormalMode
      )
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      guarantorNotRequiredEuGuard(
        onEuNotRequired = redirect(HowMovementTransported.FixedTransportInstallations, mode),
        default = formProvider().bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, mode))),
          answer => redirect(answer, mode)
        )
      )
    }

  private def cleanseAnswers(answer: HowMovementTransported)(implicit request: DataRequest[_]): UserAnswers = {
    //Cond156 - cleanup any existing TU entries when the user selects FixedTransportInstallations - set the Transport Unit type to be FixedTransportInstallations
    if(answer == FixedTransportInstallations) {
      request.userAnswers.remove(JourneyTypeSection).resetIndexedSection(TransportUnitsSection, Index(0)).set(
        TransportUnitTypePage(Index(0)), FixedTransport
      )
    } else if(request.userAnswers.get(HowMovementTransportedPage).contains(FixedTransportInstallations)) {
      //If the user previously selected Fixed Transport Installation then clear the TU section (because the user did not actively enter any TU info)
      request.userAnswers.remove(JourneyTypeSection).resetIndexedSection(TransportUnitsSection, Index(0))
    } else {
      request.userAnswers.remove(JourneyTypeSection)
    }
  }
}
