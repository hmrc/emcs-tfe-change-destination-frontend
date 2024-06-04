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

package controllers.sections.consignee

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.consignee.ConsigneeExportEoriFormProvider
import models.Mode
import navigation.ConsigneeNavigator
import pages.sections.consignee.ConsigneeExportEoriPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.sections.consignee.ConsigneeExportEoriView

import javax.inject.Inject
import scala.concurrent.Future

class ConsigneeExportEoriController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val navigator: ConsigneeNavigator,
                                       override val auth: AuthAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       override val withMovement: MovementAction,
                                       override val betaAllowList: BetaAllowListAction,
                                       formProvider: ConsigneeExportEoriFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: ConsigneeExportEoriView
                                     ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovement(ern, draftId) { implicit request =>
      Ok(view(fillForm(ConsigneeExportEoriPage, formProvider()), routes.ConsigneeExportEoriController.onSubmit(request.ern, request.arc, mode)))
    }

  def onSubmit(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, draftId) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, routes.ConsigneeExportEoriController.onSubmit(request.ern, request.arc, mode)))),
        saveAndRedirect(ConsigneeExportEoriPage, _, mode)
      )
    }
}
