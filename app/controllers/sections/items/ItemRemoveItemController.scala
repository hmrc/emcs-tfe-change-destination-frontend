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
import forms.sections.items.ItemRemoveItemFormProvider
import models.Index
import models.requests.DataRequest
import navigation.ItemsNavigator
import pages.sections.items.ItemsSectionItem
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.items.ItemRemoveItemView

import javax.inject.Inject
import scala.concurrent.Future

class ItemRemoveItemController @Inject()(
                                          override val messagesApi: MessagesApi,
                                          override val userAnswersService: UserAnswersService,
                                          override val userAllowList: UserAllowListAction,
                                          override val navigator: ItemsNavigator,
                                          override val auth: AuthAction,
                                          override val getData: DataRetrievalAction,
                                          override val requireData: DataRequiredAction,
                                          formProvider: ItemRemoveItemFormProvider,
                                          val controllerComponents: MessagesControllerComponents,
                                          view: ItemRemoveItemView
                                        ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Index): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndexAsync(idx) {
        renderView(Ok, formProvider())(ern, arc, idx)
      }
    }

  def onSubmit(ern: String, arc: String, idx: Index): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndexAsync(idx) {
        formProvider().bindFromRequest().fold(
          renderView(BadRequest, _)(ern, arc, idx),
          handleAnswerRemovalAndRedirect(_, idx)(ern, arc)
        )
      }
    }

  private def renderView(status: Status, form: Form[_])(ern: String, arc: String, idx: Index)
                        (implicit request: DataRequest[_]): Future[Result] = {
    Future.successful(
      status(view(
        form = form,
        action = routes.ItemRemoveItemController.onSubmit(ern, arc, idx)
      ))
    )
  }

  private def handleAnswerRemovalAndRedirect(shouldRemoveItem: Boolean, index: Index)(ern: String, arc: String)
                                            (implicit request: DataRequest[_]): Future[Result] = {
    if (shouldRemoveItem) {
      val cleansedAnswers = request.userAnswers.remove(ItemsSectionItem(index))
      userAnswersService.set(cleansedAnswers).map {
        _ => Redirect(routes.ItemsIndexController.onPageLoad(ern, arc))
      }
    } else {
      Future(Redirect(routes.ItemsAddToListController.onPageLoad(request.ern, request.arc)))
    }
  }
}
