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
import forms.sections.consignee.ConsigneeExportVatFormProvider
import models.Mode
import models.requests.DataRequest
import navigation.ConsigneeNavigator
import pages.sections.consignee.ConsigneeExportVatPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.consignee.ConsigneeExportVatView

import javax.inject.Inject
import scala.concurrent.Future

class ConsigneeExportVatController @Inject()(
                                              override val messagesApi: MessagesApi,
                                              override val userAnswersService: UserAnswersService,
                                              override val navigator: ConsigneeNavigator,
                                              override val auth: AuthAction,
                                              override val getData: DataRetrievalAction,
                                              override val requireData: DataRequiredAction,
                                              override val withMovement: MovementAction,
                                              override val betaAllowList: BetaAllowListAction,
                                              formProvider: ConsigneeExportVatFormProvider,
                                              val controllerComponents: MessagesControllerComponents,
                                              view: ConsigneeExportVatView
                                            ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      renderView(Ok, fillForm(ConsigneeExportVatPage, formProvider()), mode)
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        renderView(BadRequest, _, mode),
        saveAndRedirect(ConsigneeExportVatPage, _, mode)
      )
    }

  private def renderView(status: Status, form: Form[_], mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
    Future.successful(
      status(view(
        form = form,
        action = controllers.sections.consignee.routes.ConsigneeExportVatController.onSubmit(request.ern, request.arc, mode)
      ))
    )
}
