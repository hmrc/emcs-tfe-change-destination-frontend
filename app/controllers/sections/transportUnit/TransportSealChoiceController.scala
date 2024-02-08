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

package controllers.sections.transportUnit

import controllers.actions._
import forms.sections.transportUnit.TransportSealChoiceFormProvider
import models.requests.DataRequest
import models.sections.transportUnit.TransportUnitType
import models.{Index, Mode}
import navigation.TransportUnitNavigator
import pages.sections.transportUnit._
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.transportUnit.TransportSealChoiceView

import javax.inject.Inject
import scala.concurrent.Future

class TransportSealChoiceController @Inject()(override val messagesApi: MessagesApi,
                                              override val userAnswersService: UserAnswersService,
                                              override val betaAllowList: BetaAllowListAction,
                                              override val navigator: TransportUnitNavigator,
                                              override val auth: AuthAction,
                                              override val getData: DataRetrievalAction,
                                              override val requireData: DataRequiredAction,
                                              override val withMovement: MovementAction,
                                              formProvider: TransportSealChoiceFormProvider,
                                              val controllerComponents: MessagesControllerComponents,
                                              view: TransportSealChoiceView
                                             ) extends BaseTransportUnitNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      validateIndex(idx) {
        withAnswerAsync(
          page = TransportUnitTypePage(idx),
          redirectRoute = controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(request.ern, request.arc)
        ) { transportUnitType =>
          renderView(Ok, fillForm(TransportSealChoicePage(idx), formProvider(transportUnitType)), transportUnitType, idx, mode)
        }
      }
    }

  def onSubmit(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      validateIndex(idx) {
        withAnswerAsync(
          page = TransportUnitTypePage(idx),
          redirectRoute = controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(request.ern, request.arc)
        ) { transportUnitType =>
          formProvider(transportUnitType).bindFromRequest().fold(
            renderView(BadRequest, _, transportUnitType, idx, mode),
            isACommercialSeal => {
              val cleansedAnswers =
                cleanseUserAnswersIfValueHasChanged(TransportSealChoicePage(idx), isACommercialSeal, request.userAnswers.remove(TransportSealTypePage(idx)))
              saveAndRedirect(TransportSealChoicePage(idx), isACommercialSeal, cleansedAnswers, mode)
            }
          )
        }
      }
    }

  private def renderView(
                          status: Status, form: Form[_], transportUnitType: TransportUnitType, idx: Index, mode: Mode
                        )(implicit request: DataRequest[_]): Future[Result] = {
    Future.successful(status(view(
      form = form,
      mode = mode,
      transportUnitType = transportUnitType,
      onSubmitCall = controllers.sections.transportUnit.routes.TransportSealChoiceController.onSubmit(request.ern, request.arc, idx, mode)
    )))
  }
}
