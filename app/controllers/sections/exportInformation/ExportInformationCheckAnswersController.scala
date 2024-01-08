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

package controllers.sections.exportInformation

import controllers.BaseNavigationController
import controllers.actions._
import models.NormalMode
import navigation.ExportInformationNavigator
import pages.sections.exportInformation.ExportInformationCheckAnswersPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import viewmodels.checkAnswers.sections.exportInformation.ExportInformationCheckAnswersHelper
import views.html.sections.exportInformation.ExportInformationCheckAnswersView

import javax.inject.Inject

class ExportInformationCheckAnswersController @Inject()(
                                                         override val messagesApi: MessagesApi,
                                                         override val userAnswersService: UserAnswersService,
                                                         override val navigator: ExportInformationNavigator,
                                                         override val auth: AuthAction,
                                                         override val getData: DataRetrievalAction,
                                                         override val requireData: DataRequiredAction,
                                                         override val withMovement: MovementAction,
                                                         override val userAllowList: UserAllowListAction,
                                                         val cyaHelper: ExportInformationCheckAnswersHelper,
                                                         val controllerComponents: MessagesControllerComponents,
                                                         view: ExportInformationCheckAnswersView
                                                       ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovement(ern, arc) { implicit request =>
      Ok(view(
        cyaHelper.summaryList(),
        routes.ExportInformationCheckAnswersController.onSubmit(ern, arc)
      ))
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovement(ern, arc) { implicit request =>
      Redirect(navigator.nextPage(ExportInformationCheckAnswersPage, NormalMode, request.userAnswers))
    }
}
