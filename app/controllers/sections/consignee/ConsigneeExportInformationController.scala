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

package controllers.sections.consignee

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.consignee.ConsigneeExportInformationFormProvider
import models.Mode
import navigation.ConsigneeNavigator
import pages.sections.consignee.ConsigneeExportInformationPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.sections.consignee.ConsigneeExportInformationView

import javax.inject.Inject
import scala.concurrent.Future

class ConsigneeExportInformationController @Inject()(
                                              override val messagesApi: MessagesApi,
                                              override val userAnswersService: UserAnswersService,
                                              override val navigator: ConsigneeNavigator,
                                              override val auth: AuthAction,
                                              override val getData: DataRetrievalAction,
                                              override val requireData: DataRequiredAction,
                                              override val withMovement: MovementAction,
                                              override val betaAllowList: BetaAllowListAction,
                                              formProvider: ConsigneeExportInformationFormProvider,
                                              val controllerComponents: MessagesControllerComponents,
                                              view: ConsigneeExportInformationView
                                            ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovement(ern, arc) { implicit request =>
      Ok(view(fillForm(ConsigneeExportInformationPage, formProvider()), mode))
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),
        value =>
          saveAndRedirect(ConsigneeExportInformationPage, value, mode)
      )
    }
}
