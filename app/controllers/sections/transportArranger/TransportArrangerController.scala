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

package controllers.sections.transportArranger

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.transportArranger.TransportArrangerFormProvider
import models.requests.DataRequest
import models.sections.transportArranger.TransportArranger
import models.sections.transportArranger.TransportArranger.{Consignee, Consignor}
import models.{Mode, UserAnswers}
import navigation.TransportArrangerNavigator
import pages.sections.transportArranger.{TransportArrangerAddressPage, TransportArrangerNamePage, TransportArrangerPage, TransportArrangerVatPage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.sections.transportArranger.TransportArrangerView

import javax.inject.Inject
import scala.concurrent.Future

class TransportArrangerController @Inject()(
                                             override val messagesApi: MessagesApi,
                                             override val userAnswersService: UserAnswersService,
                                             override val navigator: TransportArrangerNavigator,
                                             override val auth: AuthAction,
                                             override val getData: DataRetrievalAction,
                                             override val requireData: DataRequiredAction,
                                             override val withMovement: MovementAction,
                                             formProvider: TransportArrangerFormProvider,
                                             val controllerComponents: MessagesControllerComponents,
                                             view: TransportArrangerView
                                           ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovement(ern, arc) { implicit request =>
      Ok(view(fillForm(TransportArrangerPage, formProvider()), mode))
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),
        value => {
          val cleansedAnswers = deletionLogic(value)
          saveAndRedirect(TransportArrangerPage, value, cleansedAnswers, mode)
        }
      )
    }

  private def deletionLogic(answer: TransportArranger)(implicit request: DataRequest[_]): UserAnswers =
    answer match {
      case Consignor | Consignee =>
        request.userAnswers
          .remove(TransportArrangerNamePage)
          .remove(TransportArrangerVatPage)
          .remove(TransportArrangerAddressPage)
      case _ =>
        request.userAnswers
    }

}
