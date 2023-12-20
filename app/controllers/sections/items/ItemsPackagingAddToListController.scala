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

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.items.ItemsPackagingAddToListFormProvider
import models.requests.DataRequest
import models.sections.items.ItemsPackagingAddToList
import models.{Index, NormalMode}
import navigation.ItemsNavigator
import pages.sections.items.{ItemsPackagingAddToListPage, ItemsPackagingSection}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import queries.ItemsPackagingCount
import services.UserAnswersService
import viewmodels.helpers.ItemsPackagingAddToListHelper
import viewmodels.taskList.InProgress
import views.html.sections.items.ItemsPackagingAddToListView

import javax.inject.Inject
import scala.concurrent.Future

class ItemsPackagingAddToListController @Inject()(
                                                   override val messagesApi: MessagesApi,
                                                   override val userAnswersService: UserAnswersService,
                                                   override val userAllowList: UserAllowListAction,
                                                   override val navigator: ItemsNavigator,
                                                   override val auth: AuthAction,
                                                   override val getData: DataRetrievalAction,
                                                   override val requireData: DataRequiredAction,
                                                   formProvider: ItemsPackagingAddToListFormProvider,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   view: ItemsPackagingAddToListView,
                                                   addToListHelper: ItemsPackagingAddToListHelper
                                                 ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, itemIdx: Index): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      val form = onMax(itemIdx)(maxF = None, notMaxF = Some(fillForm(ItemsPackagingAddToListPage(itemIdx), formProvider())))
      renderView(Ok, form, itemIdx)
    }

  def onSubmit(ern: String, arc: String, itemIdx: Index): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      onMax(itemIdx)(
        maxF = Future.successful(Redirect(navigator.nextPage(
          page = ItemsPackagingAddToListPage(itemIdx),
          mode = NormalMode,
          userAnswers = request.userAnswers
        ))),
        notMaxF =
          formProvider().bindFromRequest().fold(
            formWithErrors => renderView(BadRequest, Some(formWithErrors), itemIdx),
            handleSubmissionRedirect(_, itemIdx)
          )
      )
    }

  private def renderView(status: Status, form: Option[Form[_]], itemIdx: Index)(implicit request: DataRequest[_]): Future[Result] =
    Future.successful(status(view(
      formOpt = form,
      onSubmitCall = routes.ItemsPackagingAddToListController.onSubmit(request.ern, request.arc, itemIdx),
      packages = addToListHelper.allPackagesSummary(itemIdx),
      showNoOption = ItemsPackagingSection(itemIdx).status != InProgress,
      itemIdx = itemIdx
    )))

  private def handleSubmissionRedirect(answer: ItemsPackagingAddToList, itemIdx: Index)(implicit request: DataRequest[_]): Future[Result] = {
    answer match {
      case ItemsPackagingAddToList.Yes =>
        userAnswersService.set(request.userAnswers.remove(ItemsPackagingAddToListPage(itemIdx))).map { _ =>
          Redirect(navigator.nextPage(
            page = ItemsPackagingAddToListPage(itemIdx),
            mode = NormalMode,
            userAnswers = request.userAnswers.set(ItemsPackagingAddToListPage(itemIdx), ItemsPackagingAddToList.Yes)
          ))
        }
      case value =>
        saveAndRedirect(ItemsPackagingAddToListPage(itemIdx), value, NormalMode)
    }
  }

  private def onMax[T](itemIdx: Index)(maxF: => T, notMaxF: => T)(implicit dataRequest: DataRequest[_]): T = {
    dataRequest.userAnswers.get(ItemsPackagingCount(itemIdx)) match {
      case Some(value) if value >= ItemsPackagingSection(itemIdx).MAX => maxF
      case _ => notMaxF
    }
  }
}
