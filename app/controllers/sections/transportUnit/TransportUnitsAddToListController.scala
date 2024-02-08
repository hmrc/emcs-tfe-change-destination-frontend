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

package controllers.sections.transportUnit

import controllers.actions._
import forms.sections.transportUnit.TransportUnitsAddToListFormProvider
import models.NormalMode
import models.requests.DataRequest
import models.sections.transportUnit.TransportUnitsAddToListModel
import navigation.TransportUnitNavigator
import pages.sections.transportUnit.{TransportUnitsAddToListPage, TransportUnitsSection}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import queries.TransportUnitsCount
import services.UserAnswersService
import viewmodels.helpers.TransportUnitsAddToListHelper
import views.html.sections.transportUnit.TransportUnitsAddToListView

import javax.inject.Inject
import scala.concurrent.Future

class TransportUnitsAddToListController @Inject()(
                                                   override val messagesApi: MessagesApi,
                                                   override val userAnswersService: UserAnswersService,
                                                   override val betaAllowList: BetaAllowListAction,
                                                   override val navigator: TransportUnitNavigator,
                                                   override val auth: AuthAction,
                                                   override val getData: DataRetrievalAction,
                                                   override val requireData: DataRequiredAction,
                                                   override val withMovement: MovementAction,
                                                   formProvider: TransportUnitsAddToListFormProvider,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   view: TransportUnitsAddToListView,
                                                   summaryHelper: TransportUnitsAddToListHelper
                                                 ) extends BaseTransportUnitNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovement(ern, arc) { implicit request =>
      val form = onMax(ifMax = None, ifNotMax = Some(fillForm(TransportUnitsAddToListPage, formProvider())))

      Ok(view(form, summaryHelper.allTransportUnitsSummary(onReviewPage = false), NormalMode))
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      onMax(
        ifMax = Future.successful(Redirect(controllers.routes.TaskListController.onPageLoad(ern, arc))),
        ifNotMax = formProvider().bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(Some(formWithErrors), summaryHelper.allTransportUnitsSummary(onReviewPage = false), NormalMode))),
          {
            case TransportUnitsAddToListModel.Yes =>
              userAnswersService.set(request.userAnswers.remove(TransportUnitsAddToListPage))
                .map { ua =>
                  Redirect(navigator
                    .nextPage(TransportUnitsAddToListPage, NormalMode, ua.set(TransportUnitsAddToListPage, TransportUnitsAddToListModel.Yes)))
                }
            case value =>
              saveAndRedirect(TransportUnitsAddToListPage, value, NormalMode)
          }
        )
      )

    }

  private def onMax[T](ifMax: => T, ifNotMax: => T)(implicit dataRequest: DataRequest[_]): T = {
    dataRequest.userAnswers.get(TransportUnitsCount) match {
      case Some(value) if value >= TransportUnitsSection.MAX => ifMax
      case _ => ifNotMax
    }
  }
}
