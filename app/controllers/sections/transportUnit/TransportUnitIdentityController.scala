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
import forms.sections.transportUnit.TransportUnitIdentityFormProvider
import models.requests.DataRequest
import models.sections.transportUnit.TransportUnitType
import models.{Index, Mode}
import navigation.TransportUnitNavigator
import pages.sections.transportUnit.{TransportUnitIdentityPage, TransportUnitTypePage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.transportUnit.TransportUnitIdentityView

import javax.inject.Inject
import scala.concurrent.Future

class TransportUnitIdentityController @Inject()(
                                                 override val messagesApi: MessagesApi,
                                                 override val userAnswersService: UserAnswersService,
                                                 override val navigator: TransportUnitNavigator,
                                                 override val auth: AuthAction,
                                                 override val getData: DataRetrievalAction,
                                                 override val requireData: DataRequiredAction,
                                                 override val withMovement: MovementAction,
                                                 override val betaAllowList: BetaAllowListAction,
                                                 formProvider: TransportUnitIdentityFormProvider,
                                                 val controllerComponents: MessagesControllerComponents,
                                                 view: TransportUnitIdentityView
                                               ) extends BaseTransportUnitNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      validateIndex(idx) {
        withTransportUnitType(idx) { transportUnitType =>
          Future.successful(Ok(view(fillForm(TransportUnitIdentityPage(idx), formProvider(transportUnitType)), transportUnitType, idx, mode)))
        }
      }
    }

  def onSubmit(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      validateIndex(idx) {
        withTransportUnitType(idx) { transportUnitType =>
          formProvider(transportUnitType).bindFromRequest().fold(
            formWithErrors =>
              Future.successful(BadRequest(view(formWithErrors, transportUnitType, idx, mode))),
            value =>
              saveAndRedirect(TransportUnitIdentityPage(idx), value, mode)
          )
        }
      }
    }


  private def withTransportUnitType(index: Index)(f: TransportUnitType => Future[Result])(implicit request: DataRequest[_]): Future[Result] = {
    withAnswerAsync(
      page = TransportUnitTypePage(index),
      redirectRoute = controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(request.ern, request.arc)
    )(f)
  }
}
