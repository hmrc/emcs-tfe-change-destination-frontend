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

package controllers.sections.guarantor

import controllers.actions._
import models.NormalMode
import navigation.GuarantorNavigator
import pages.sections.guarantor.GuarantorCheckAnswersPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import viewmodels.checkAnswers.sections.guarantor.GuarantorCheckAnswersHelper
import views.html.sections.guarantor.GuarantorCheckAnswersView

import javax.inject.Inject

class GuarantorCheckAnswersController @Inject()(
                                                 override val messagesApi: MessagesApi,
                                                 override val userAnswersService: UserAnswersService,
                                                 override val navigator: GuarantorNavigator,
                                                 override val auth: AuthAction,
                                                 override val getData: DataRetrievalAction,
                                                 override val requireData: DataRequiredAction,
                                                 override val withMovement: MovementAction,
                                                 override val betaAllowList: BetaAllowListAction,
                                                 val cyaHelper: GuarantorCheckAnswersHelper,
                                                 val controllerComponents: MessagesControllerComponents,
                                                 view: GuarantorCheckAnswersView
                                               ) extends GuarantorBaseController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovement(ern, arc) { implicit request =>
      Ok(view(
        cyaHelper.summaryList(),
        controllers.sections.guarantor.routes.GuarantorCheckAnswersController.onSubmit(ern, arc)
      ))
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovement(ern, arc) { implicit request =>
      Redirect(navigator.nextPage(GuarantorCheckAnswersPage, NormalMode, request.userAnswers))
    }


}
