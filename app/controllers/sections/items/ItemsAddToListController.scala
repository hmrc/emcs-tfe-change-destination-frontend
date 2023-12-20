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

package controllers.sections.items

import controllers.actions._
import forms.sections.items.ItemsAddToListFormProvider
import models.NormalMode
import models.requests.DataRequest
import models.sections.items.ItemsAddToList
import navigation.ItemsNavigator
import pages.sections.items.{ItemsAddToListPage, ItemsSectionItems}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import queries.ItemsCount
import services.UserAnswersService
import viewmodels.helpers.ItemsAddToListHelper
import viewmodels.taskList.InProgress
import views.html.sections.items.ItemsAddToListView

import javax.inject.Inject
import scala.concurrent.Future

class ItemsAddToListController @Inject()(
                                          override val messagesApi: MessagesApi,
                                          override val userAnswersService: UserAnswersService,
                                          override val userAllowList: UserAllowListAction,
                                          override val navigator: ItemsNavigator,
                                          override val auth: AuthAction,
                                          override val getData: DataRetrievalAction,
                                          override val requireData: DataRequiredAction,
                                          formProvider: ItemsAddToListFormProvider,
                                          val controllerComponents: MessagesControllerComponents,
                                          view: ItemsAddToListView,
                                          addToListHelper: ItemsAddToListHelper
                                        ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) {
      removingAnyItemsWithoutEPCandCnCode(_) { implicit request =>
        withAtLeastOneItem {
          val form = onMax(maxF = None, notMaxF = Some(fillForm(ItemsAddToListPage, formProvider())))
          renderView(Ok, form)
        }
      }
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      onMax(
        maxF = Future.successful(Redirect(navigator.nextPage(
          page = ItemsAddToListPage,
          mode = NormalMode,
          userAnswers = request.userAnswers
        ))),
        notMaxF =
          formProvider().bindFromRequest().fold(
            formWithErrors => renderView(BadRequest, Some(formWithErrors)),
            handleSubmissionRedirect(_)
          )
      )
    }

  private def renderView(status: Status, form: Option[Form[_]])(implicit request: DataRequest[_]): Future[Result] =
    addToListHelper.allItemsSummary.map { itemsSummary =>
      status(view(
        formOpt = form,
        onSubmitCall = routes.ItemsAddToListController.onSubmit(request.ern, request.arc),
        items = itemsSummary,
        showNoOption = ItemsSectionItems.status != InProgress
      ))
    }

  private def handleSubmissionRedirect(answer: ItemsAddToList)(implicit request: DataRequest[_]): Future[Result] = {
    answer match {
      case ItemsAddToList.Yes =>
        userAnswersService.set(request.userAnswers.remove(ItemsAddToListPage)).map { _ =>
          Redirect(navigator.nextPage(
            page = ItemsAddToListPage,
            mode = NormalMode,
            userAnswers = request.userAnswers.set(ItemsAddToListPage, ItemsAddToList.Yes)
          ))
        }
      case value =>
        saveAndRedirect(ItemsAddToListPage, value, NormalMode)
    }
  }

  private def onMax[T](maxF: => T, notMaxF: => T)(implicit request: DataRequest[_]): T = {
    request.userAnswers.get(ItemsCount) match {
      case Some(value) if value >= ItemsSectionItems.MAX => maxF
      case _ => notMaxF
    }
  }

  private def withAtLeastOneItem(f: => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    request.userAnswers.get(ItemsCount) match {
      case Some(value) if value > 0 => f
      case _ => Future.successful(Redirect(routes.ItemsIndexController.onPageLoad(request.ern, request.arc)))
    }
}
