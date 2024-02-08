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
import forms.sections.info.DispatchDetailsFormProvider
import models.Mode
import models.requests.DataRequest
import navigation.InformationNavigator
import pages.sections.info.DispatchDetailsPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc._
import services.{PreDraftService, UserAnswersService}
import views.html.sections.info.DispatchDetailsView

import javax.inject.Inject
import scala.concurrent.Future

class DispatchDetailsController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           val preDraftService: PreDraftService,
                                           val navigator: InformationNavigator,
                                           val auth: AuthAction,
                                           val getPreDraftData: PreDraftDataRetrievalAction,
                                           val requirePreDraftData: PreDraftDataRequiredAction,
                                           val getData: DataRetrievalAction,
                                           val requireData: DataRequiredAction,
                                           val withMovement: MovementAction,
                                           formProvider: DispatchDetailsFormProvider,
                                           val userAnswersService: UserAnswersService,
                                           val controllerComponents: MessagesControllerComponents,
                                           view: DispatchDetailsView,
                                           val betaAllowList: BetaAllowListAction
                                         ) extends BasePreDraftNavigationController with AuthActionHelper with PreDraftAuthActionHelper {

  def onPreDraftPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedWithPreDraftDataUpToDateMovementAsync(ern, arc) { implicit request =>
      renderViewPreDraft(
        Ok,
        fillForm(DispatchDetailsPage(), formProvider()),
        controllers.sections.info.routes.DispatchDetailsController.onPreDraftSubmit(request.ern, request.arc, mode),
        mode
      )
    }


  def onPreDraftSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedWithPreDraftDataUpToDateMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          renderViewPreDraft(
            BadRequest,
            formWithErrors,
            controllers.sections.info.routes.DispatchDetailsController.onPreDraftSubmit(request.ern, request.arc, mode),
            mode
          ),
        value =>
          //TODO: Temporarily reinitialise the protected mongo UserAnswers (draft CoD answers).
          //      This will get replaced with proper logic once the Change of Destination INFO section is built properly!!!
          //      ALSO! If you click the "Skip Link" it won't hit this code and will therefore fail. But none of this will
          //      be relevant in the future.
          userAnswersService.set(request.userAnswers).flatMap { _ =>
            savePreDraftAndRedirect(DispatchDetailsPage(), value, mode)
          }
      )
    }

  def renderViewPreDraft(status: Status, form: Form[_], onSubmitCall: Call, mode: Mode)
                        (implicit request: DataRequest[_]): Future[Result] = {
    Future.successful(
      status(view(
        form = form,
        deferredMovement = false, //TODO: This can't be worked out, so needs to be removed
        onSubmitCall = onSubmitCall,
        skipQuestionCall = navigator.nextPage(DispatchDetailsPage(), mode, request.userAnswers)
      ))
    )
  }
}
