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

package controllers.sections.transportArranger

import controllers.BaseNavigationController
import controllers.actions._
import models.NormalMode
import navigation.TransportArrangerNavigator
import pages.sections.transportArranger.TransportArrangerCheckAnswersPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import viewmodels.checkAnswers.sections.transportArranger.TransportArrangerCheckAnswersHelper
import views.html.sections.transportArranger.TransportArrangerCheckAnswersView

import javax.inject.Inject

class TransportArrangerCheckAnswersController @Inject()(
                                                         override val messagesApi: MessagesApi,
                                                         override val userAnswersService: UserAnswersService,
                                                         override val navigator: TransportArrangerNavigator,
                                                         override val auth: AuthAction,
                                                         override val getData: DataRetrievalAction,
                                                         override val requireData: DataRequiredAction,
                                                         override val withMovement: MovementAction,
                                                         override val userAllowList: UserAllowListAction,
                                                         val cyaHelper: TransportArrangerCheckAnswersHelper,
                                                         val controllerComponents: MessagesControllerComponents,
                                                         view: TransportArrangerCheckAnswersView
                                                       ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovement(ern, arc) { implicit request =>
      Ok(view(
        cyaHelper.summaryList(),
        routes.TransportArrangerCheckAnswersController.onSubmit(ern, arc)
      ))
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovement(ern, arc) { implicit request =>
      Redirect(navigator.nextPage(TransportArrangerCheckAnswersPage, NormalMode, request.userAnswers))
    }
}
