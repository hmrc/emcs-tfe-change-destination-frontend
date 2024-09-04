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
import forms.sections.transportUnit.TransportUnitGiveMoreInformationFormProvider
import models.requests.DataRequest
import models.sections.transportUnit.TransportUnitType
import models.{Index, Mode}
import navigation.TransportUnitNavigator
import pages.sections.transportUnit.{TransportUnitGiveMoreInformationPage, TransportUnitTypePage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import utils.JsonOptionFormatter.optionFormat
import views.html.sections.transportUnit.TransportUnitGiveMoreInformationView

import javax.inject.Inject
import scala.concurrent.Future

class TransportUnitGiveMoreInformationController @Inject()(
                                                            override val messagesApi: MessagesApi,
                                                            override val userAnswersService: UserAnswersService,
                                                            override val navigator: TransportUnitNavigator,
                                                            override val auth: AuthAction,
                                                            override val getData: DataRetrievalAction,
                                                            override val requireData: DataRequiredAction,
                                                            override val withMovement: MovementAction,
                                                            formProvider: TransportUnitGiveMoreInformationFormProvider,
                                                            val controllerComponents: MessagesControllerComponents,
                                                            view: TransportUnitGiveMoreInformationView
                                                          ) extends BaseTransportUnitNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      validateIndex(idx) {
        withAnswerAsync(TransportUnitTypePage(idx)) { transportUnitType =>
          renderView(Ok, fillForm(TransportUnitGiveMoreInformationPage(idx), formProvider()), idx, mode, transportUnitType)
        }
      }
    }

  def onSubmit(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      validateIndex(idx) {
        withAnswerAsync(TransportUnitTypePage(idx)) { transportUnitType =>
          submitAndTrimWhitespaceFromTextarea(TransportUnitGiveMoreInformationPage(idx), formProvider)(
            renderView(BadRequest, _, idx, mode, transportUnitType)
          )(
            saveAndRedirect(TransportUnitGiveMoreInformationPage(idx), _, mode)
          )
        }
      }
    }


  private def renderView(status: Status, form: Form[_], idx: Index, mode: Mode, transportUnitType: TransportUnitType)
                        (implicit request: DataRequest[_]): Future[Result] = {
    Future.successful(
      status(view(
        form = form,
        mode = mode,
        idx = idx,
        transportUnitType = transportUnitType
      ))
    )
  }
}
