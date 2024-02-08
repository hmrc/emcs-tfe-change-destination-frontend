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
import forms.sections.transportArranger.TransportArrangerNameFormProvider
import models.Mode
import models.requests.DataRequest
import models.sections.transportArranger.TransportArranger
import models.sections.transportArranger.TransportArranger.{GoodsOwner, Other}
import navigation.TransportArrangerNavigator
import pages.sections.transportArranger.{TransportArrangerNamePage, TransportArrangerPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.transportArranger.TransportArrangerNameView

import javax.inject.Inject
import scala.concurrent.Future

class TransportArrangerNameController @Inject()(
                                                 override val messagesApi: MessagesApi,
                                                 override val userAnswersService: UserAnswersService,
                                                 override val navigator: TransportArrangerNavigator,
                                                 override val auth: AuthAction,
                                                 override val getData: DataRetrievalAction,
                                                 override val requireData: DataRequiredAction,
                                                 override val withMovement: MovementAction,
                                                 override val betaAllowList: BetaAllowListAction,
                                                 formProvider: TransportArrangerNameFormProvider,
                                                 val controllerComponents: MessagesControllerComponents,
                                                 view: TransportArrangerNameView
                                               ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit dataRequest =>
      withTransportArrangerAnswer { transportArrangerAnswer =>
        renderView(Ok, fillForm(TransportArrangerNamePage, formProvider()), transportArrangerAnswer, mode)
      }
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      withTransportArrangerAnswer { transportArrangerAnswer =>
        formProvider().bindFromRequest().fold(
          formWithErrors =>
            renderView(BadRequest, formWithErrors, transportArrangerAnswer, mode),
          value =>
            saveAndRedirect(TransportArrangerNamePage, value.trim, mode)
        )
      }
    }

  private def withTransportArrangerAnswer(f: TransportArranger => Future[Result])(implicit request: DataRequest[_]): Future[Result] = {
    withAnswerAsync(
      page = TransportArrangerPage,
      redirectRoute = controllers.sections.transportArranger.routes.TransportArrangerIndexController.onPageLoad(request.ern, request.arc)
    ) {
      case transportArranger@(GoodsOwner | Other) => f(transportArranger)
      case transportArranger =>
        logger.warn(s"[withTransportArrangerAnswer] Invalid answer of $transportArranger for this controller/page")
        Future.successful(
          Redirect(
            controllers.routes.JourneyRecoveryController.onPageLoad()
          )
        )
    }
  }

  private def renderView(status: Status, form: Form[_], transportArranger: TransportArranger, mode: Mode)(implicit request: DataRequest[_]): Future[Result] = {
    Future.successful(
      status(view(
        form = form,
        transportArranger = transportArranger,
        mode = mode
      ))
    )
  }

}
