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

package controllers.sections.sad

import controllers.actions._
import forms.sections.sad.SadAddToListFormProvider
import models.NormalMode
import models.requests.DataRequest
import models.sections.sad.SadAddToListModel
import navigation.SadNavigator
import pages.sections.sad.{SadAddToListPage, SadSection}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import queries.SadCount
import services.UserAnswersService
import viewmodels.helpers.SadAddToListHelper
import views.html.sections.sad.SadAddToListView

import javax.inject.Inject
import scala.concurrent.Future

class SadAddToListController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        override val userAnswersService: UserAnswersService,
                                        override val navigator: SadNavigator,
                                        override val auth: AuthAction,
                                        override val getData: DataRetrievalAction,
                                        override val requireData: DataRequiredAction,
                                        override val userAllowList: UserAllowListAction,
                                        formProvider: SadAddToListFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: SadAddToListView,
                                        summaryHelper: SadAddToListHelper
                                      ) extends BaseSadNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      val form = onMax(ifMax = None, ifNotMax = Some(fillForm(SadAddToListPage, formProvider())))

      Ok(view(form, summaryHelper.allSadSummary(), NormalMode))
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      onMax(
        ifMax = Future.successful(Redirect(controllers.routes.DraftMovementController.onPageLoad(ern, arc))),
        ifNotMax = formProvider().bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(Some(formWithErrors), summaryHelper.allSadSummary(), NormalMode))), {
            case SadAddToListModel.Yes =>
              userAnswersService.set(request.userAnswers.remove(SadAddToListPage))
                .map { ua =>
                  Redirect(navigator
                    .nextPage(SadAddToListPage, NormalMode, ua.set(SadAddToListPage, SadAddToListModel.Yes)))
                }
            case value =>
              saveAndRedirect(SadAddToListPage, value, NormalMode)
          })
      )

    }

  private def onMax[D, T](ifMax: => T, ifNotMax: => T)(implicit dataRequest: DataRequest[_]): T = {
    dataRequest.userAnswers.get(SadCount) match {
      case Some(value) if value >= SadSection.MAX => ifMax
      case _ => ifNotMax
    }
  }
}
