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

package controllers.sections.consignee

import cats.implicits._
import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.consignee.ConsigneeExciseFormProvider
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.{EuTaxWarehouse, TemporaryCertifiedConsignee, TemporaryRegisteredConsignee, RegisteredConsignee}
import models.{CountryModel, Mode}
import navigation.ConsigneeNavigator
import pages.sections.consignee.ConsigneeExcisePage
import pages.sections.info.{ChangeTypePage, DestinationTypePage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{GetMemberStatesService, UserAnswersService}
import views.html.sections.consignee.ConsigneeExciseView

import javax.inject.Inject
import scala.concurrent.Future

class ConsigneeExciseController @Inject()(override val messagesApi: MessagesApi,
                                          override val auth: AuthAction,
                                          override val getData: DataRetrievalAction,
                                          override val requireData: DataRequiredAction,
                                          override val withMovement: MovementAction,
                                          override val navigator: ConsigneeNavigator,
                                          override val userAnswersService: UserAnswersService,
                                          formProvider: ConsigneeExciseFormProvider,
                                          val controllerComponents: MessagesControllerComponents,
                                          memberStatesService: GetMemberStatesService,
                                          view: ConsigneeExciseView
                                         ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      withOptionalEuMemberStates { memberStates =>
        withAnswerAsync(DestinationTypePage) {
          movementScenario =>
            withAnswerAsync(ChangeTypePage) {
              changeType =>
                Future.successful(Ok(view(
                  fillForm(ConsigneeExcisePage, formProvider(memberStates, movementScenario, changeType)),
                  routes.ConsigneeExciseController.onSubmit(ern, arc, mode),
                  isNorthernIrishTemporaryRegisteredConsignee,
                  isNorthernIrishTemporaryCertifiedConsignee
                )))
            }
        }
      }
    }


  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      withOptionalEuMemberStates { memberStates =>
        withAnswerAsync(DestinationTypePage) {
          movementScenario =>
            withAnswerAsync(ChangeTypePage) {
              changeType =>
                formProvider(memberStates, movementScenario, changeType).bindFromRequest().fold(
                  formWithErrors =>
                    Future.successful(
                      BadRequest(view(
                        formWithErrors,
                        routes.ConsigneeExciseController.onSubmit(ern, arc, mode),
                        isNorthernIrishTemporaryRegisteredConsignee,
                        isNorthernIrishTemporaryCertifiedConsignee
                      ))
                    ),
                  exciseRegistrationNumber =>
                    saveAndRedirect(ConsigneeExcisePage, exciseRegistrationNumber, mode)
                )
            }
        }
      }
    }

  private def withOptionalEuMemberStates[A](f: Option[Seq[CountryModel]] => Future[A])(implicit request: DataRequest[_]): Future[A] =
    Option.when(DestinationTypePage.value.contains(EuTaxWarehouse) || DestinationTypePage.value.contains(RegisteredConsignee)) {
      memberStatesService.getEuMemberStates()
    }.traverse(identity).flatMap(f)

  private def isNorthernIrishTemporaryRegisteredConsignee(implicit request: DataRequest[_]) =
    request.userTypeFromErn.isNorthernIrelandErn &&
      DestinationTypePage.value.contains(TemporaryRegisteredConsignee)

  private def isNorthernIrishTemporaryCertifiedConsignee(implicit request: DataRequest[_]) =
    request.userTypeFromErn.isNorthernIrelandErn &&
      DestinationTypePage.value.contains(TemporaryCertifiedConsignee)

}
