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

package controllers.sections.info

import controllers.BasePreDraftNavigationController
import controllers.actions._
import controllers.actions.predraft.{PreDraftAuthActionHelper, PreDraftDataRequiredAction, PreDraftDataRetrievalAction}
import forms.sections.info.DispatchPlaceFormProvider
import models.requests.DataRequest
import models.{Mode, NormalMode}
import navigation.InformationNavigator
import pages.sections.info.DispatchPlacePage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{PreDraftService, UserAnswersService}
import views.html.sections.info.DispatchPlaceView

import javax.inject.Inject
import scala.concurrent.Future

class DispatchPlaceController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         val preDraftService: PreDraftService,
                                         val userAnswersService: UserAnswersService,
                                         val navigator: InformationNavigator,
                                         val auth: AuthAction,
                                         val getPreDraftData: PreDraftDataRetrievalAction,
                                         val requirePreDraftData: PreDraftDataRequiredAction,
                                         val getData: DataRetrievalAction,
                                         val requireData: DataRequiredAction,
                                         val withMovement: MovementAction,
                                         formProvider: DispatchPlaceFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: DispatchPlaceView,
                                         val userAllowList: UserAllowListAction
                                       ) extends BasePreDraftNavigationController with AuthActionHelper with PreDraftAuthActionHelper {

  def onPreDraftPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedWithPreDraftDataUpToDateMovementAsync(ern, arc) { implicit request =>
      renderView(Ok, fillForm(DispatchPlacePage, formProvider()), mode)
    }

  def onPreDraftSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedWithPreDraftDataUpToDateMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          renderView(BadRequest, formWithErrors, mode),
        value =>
          savePreDraftAndRedirect(DispatchPlacePage, value, mode)
      )
    }

  def renderView(status: Status, form: Form[_], mode: Mode)(implicit request: DataRequest[_]): Future[Result] = {
    withGuard {
      Future.successful(
        status(view(form, controllers.sections.info.routes.DispatchPlaceController.onPreDraftSubmit(request.ern, request.arc, mode)))
      )
    }
  }

  private def withGuard(f: => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    if (request.isNorthernIrelandErn) {
      f
    } else {
      Future.successful(
        Redirect(controllers.sections.info.routes.DestinationTypeController.onPreDraftPageLoad(request.ern, request.arc, NormalMode))
      )
    }
}




