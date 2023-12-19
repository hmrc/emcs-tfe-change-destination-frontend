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

package controllers.sections.dispatch

import controllers.BaseNavigationController
import controllers.actions._
import models.NormalMode
import navigation.DispatchNavigator
import pages.sections.dispatch.DispatchCheckAnswersPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import viewmodels.checkAnswers.sections.dispatch.DispatchCheckAnswersHelper
import views.html.sections.dispatch.DispatchCheckAnswersView

import javax.inject.Inject

class DispatchCheckAnswersController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                override val userAnswersService: UserAnswersService,
                                                override val userAllowList: UserAllowListAction,
                                                override val navigator: DispatchNavigator,
                                                override val auth: AuthAction,
                                                override val getData: DataRetrievalAction,
                                                override val requireData: DataRequiredAction,
                                                val controllerComponents: MessagesControllerComponents,
                                                val dispatchCheckAnswersHelper: DispatchCheckAnswersHelper,
                                                view: DispatchCheckAnswersView
                                              ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      Ok(view(
        list = dispatchCheckAnswersHelper.summaryList(),
        onSubmitCall = controllers.sections.dispatch.routes.DispatchCheckAnswersController.onSubmit(ern, arc)
      ))
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      Redirect(navigator.nextPage(DispatchCheckAnswersPage, NormalMode, request.userAnswers))
    }
}
