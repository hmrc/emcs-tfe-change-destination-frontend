/*
 * Copyright 2024 HM Revenue & Customs
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
import forms.sections.info.NewDestinationTypeFormProvider
import models.UserType.NorthernIrelandWarehouseKeeper
import models.requests.DataRequest
import models.sections.info.DispatchPlace.GreatBritain
import navigation.InformationNavigator
import pages.sections.info.DestinationTypePage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{PreDraftService, UserAnswersService}
import views.html.sections.info.NewDestinationTypeView

import javax.inject.Inject
import scala.concurrent.Future

class NewDestinationTypeController @Inject()(
                                              override val messagesApi: MessagesApi,
                                              val preDraftService: PreDraftService,
                                              val navigator: InformationNavigator,
                                              val userAnswersService: UserAnswersService,
                                              val auth: AuthAction,
                                              val getPreDraftData: PreDraftDataRetrievalAction,
                                              val requirePreDraftData: PreDraftDataRequiredAction,
                                              val getData: DataRetrievalAction,
                                              val requireData: DataRequiredAction,
                                              val withMovement: MovementAction,
                                              formProvider: NewDestinationTypeFormProvider,
                                              val controllerComponents: MessagesControllerComponents,
                                              view: NewDestinationTypeView
                                            ) extends BasePreDraftNavigationController with AuthActionHelper with PreDraftAuthActionHelper {

  def onPreDraftPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedWithPreDraftDataUpToDateMovementAsync(ern, arc) { implicit request =>
      renderView(Ok, fillFormFromUserAnswersOnly(DestinationTypePage, formProvider()))
    }

  def onPreDraftSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedWithPreDraftDataUpToDateMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          renderView(BadRequest, formWithErrors),
        value =>
          createDraftEntryAndRedirect(DestinationTypePage, value)
      )
    }

  private[info] def renderView(status: Status, form: Form[_])(implicit request: DataRequest[_]): Future[Result] = {
    Future.successful(
      request.dispatchPlace match {
        case _ if request.userTypeFromErn != NorthernIrelandWarehouseKeeper =>
          // GB ERN or XIRC ERN
          status(view(GreatBritain, form, controllers.sections.info.routes.NewDestinationTypeController.onPreDraftSubmit(request.ern, request.arc)))
        case dispatchPlace =>
          // XIWK ERN
          status(view(dispatchPlace, form, controllers.sections.info.routes.NewDestinationTypeController.onPreDraftSubmit(request.ern, request.arc)))
      }
    )
  }
}
