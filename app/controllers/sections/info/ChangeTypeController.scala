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
import forms.sections.info.ChangeTypeFormProvider
import models.NormalMode
import models.requests.DataRequest
import models.sections.info.ChangeType.{ChangeConsignee, ReturnToConsignor}
import models.sections.info.movementScenario.MovementScenario.ReturnToThePlaceOfDispatch
import navigation.InformationNavigator
import pages.sections.info.{ChangeTypePage, DestinationTypePage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{PreDraftService, UserAnswersService}
import views.html.sections.info.ChangeTypeView

import javax.inject.Inject
import scala.concurrent.Future

class ChangeTypeController @Inject()(
                                      override val messagesApi: MessagesApi,
                                      override val userAnswersService: UserAnswersService,
                                      override val navigator: InformationNavigator,
                                      override val auth: AuthAction,
                                      override val preDraftService: PreDraftService,
                                      override val getPreDraftData: PreDraftDataRetrievalAction,
                                      override val requirePreDraftData: PreDraftDataRequiredAction,
                                      override val withMovement: MovementAction,
                                      formProvider: ChangeTypeFormProvider,
                                      val controllerComponents: MessagesControllerComponents,
                                      view: ChangeTypeView
                                    ) extends BasePreDraftNavigationController with PreDraftAuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedWithPreDraftDataUpToDateMovementAsync(ern, arc) { implicit request =>
      renderView(Ok, fillForm(ChangeTypePage, formProvider()))
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedWithPreDraftDataUpToDateMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        renderView(BadRequest, _),
        {
          case ReturnToConsignor =>
            //If the change type is Return to Consignor then set the DestinationType to Return to Place of Dispatch automatically for the User
            //we delete the pre-draft at this point too as the service then redirects to the Task List page
            for {
              savedAnswers <- userAnswersService.set(
                request.userAnswers
                  .set(ChangeTypePage, ReturnToConsignor)
                  .set(DestinationTypePage, ReturnToThePlaceOfDispatch)
              )
              _ <- preDraftService.clear(ern, arc)
            } yield {
              Redirect(navigator.nextPage(ChangeTypePage, NormalMode, savedAnswers))
            }
          case ChangeConsignee =>
            savePreDraftAndRedirect(ChangeTypePage, ChangeConsignee, NormalMode)
          case changeType =>
            createDraftEntryAndRedirect(ChangeTypePage, changeType)
        }
      )
    }

  private def renderView(status: Status, form: Form[_])(implicit request: DataRequest[_]): Future[Result] =
    Future(status(view(form, routes.ChangeTypeController.onSubmit(request.ern, request.arc))))
}
