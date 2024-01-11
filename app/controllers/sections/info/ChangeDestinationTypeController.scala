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
import forms.sections.info.ChangeDestinationTypeFormProvider
import models.NormalMode
import models.requests.DataRequest
import navigation.InformationNavigator
import pages.sections.info.ChangeDestinationTypePage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{PreDraftService, UserAnswersService}
import viewmodels.checkAnswers.sections.info.ChangeDestinationTypeHelper
import views.html.sections.info.ChangeDestinationTypeView

import javax.inject.Inject
import scala.concurrent.Future

class ChangeDestinationTypeController @Inject()(
                                                 override val messagesApi: MessagesApi,
                                                 override val userAnswersService: UserAnswersService,
                                                 override val navigator: InformationNavigator,
                                                 override val auth: AuthAction,
                                                 override val withMovement: MovementAction,
                                                 override val getData: DataRetrievalAction,
                                                 override val requireData: DataRequiredAction,
                                                 override val userAllowList: UserAllowListAction,
                                                 val getPreDraftData: PreDraftDataRetrievalAction,
                                                 val requirePreDraftData: PreDraftDataRequiredAction,
                                                 val preDraftService: PreDraftService,
                                                 formProvider: ChangeDestinationTypeFormProvider,
                                                 val controllerComponents: MessagesControllerComponents,
                                                 view: ChangeDestinationTypeView,
                                                 summaryListHelper: ChangeDestinationTypeHelper
                                     ) extends BasePreDraftNavigationController with AuthActionHelper with PreDraftAuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedWithPreDraftDataUpToDateMovementAsync(ern, arc) { implicit request =>
      renderView(Ok, fillForm(ChangeDestinationTypePage, formProvider()))
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedWithPreDraftDataCachedMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        renderView(BadRequest, _),
        {
          case value@true =>
            savePreDraftAndRedirect(ChangeDestinationTypePage, value, NormalMode)
          case value@false =>
            userAnswersService.set(request.userAnswers.set(ChangeDestinationTypePage, value)).flatMap { _ =>
              savePreDraftAndRedirect(ChangeDestinationTypePage, value, NormalMode)
            }
        }
      )
    }

  private def renderView(status: Status, form: Form[_])(implicit request: DataRequest[_]): Future[Result] = {
    val summaryList = summaryListHelper.summaryList()
    Future.successful(status(view(form, summaryList, controllers.sections.info.routes.ChangeDestinationTypeController.onSubmit(request.ern, request.arc))))
  }
}
