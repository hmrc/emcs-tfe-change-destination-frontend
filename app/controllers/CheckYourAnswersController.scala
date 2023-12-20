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

package controllers

import config.SessionKeys.SUBMISSION_RECEIPT_REFERENCE
import controllers.actions._
import models.NormalMode
import navigation.Navigator
import pages.CheckAnswersPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import viewmodels.helpers.CheckAnswersHelper
import views.html.CheckYourAnswersView

import javax.inject.Inject
import scala.concurrent.Future

class CheckYourAnswersController @Inject()(override val messagesApi: MessagesApi,
                                           override val auth: AuthAction,
                                           override val userAllowList: UserAllowListAction,
                                           override val getData: DataRetrievalAction,
                                           override val requireData: DataRequiredAction,
                                           val controllerComponents: MessagesControllerComponents,
                                           val navigator: Navigator,
                                           view: CheckYourAnswersView,
                                           checkAnswersHelper: CheckAnswersHelper
                                          ) extends BaseController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      Future.successful(Ok(view(
        routes.CheckYourAnswersController.onSubmit(ern, arc),
        checkAnswersHelper.summaryList(Seq.empty)
      )))
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      //TODO: Add Call to Submission Service and replace `PLACEHOLDER` with receipt from Downstream
      Redirect(navigator.nextPage(CheckAnswersPage, NormalMode, request.userAnswers))
        .addingToSession(SUBMISSION_RECEIPT_REFERENCE -> "PLACEHOLDER")
    }
}
