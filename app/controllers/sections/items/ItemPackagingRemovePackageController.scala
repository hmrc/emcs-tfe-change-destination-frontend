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
import forms.sections.items.ItemPackagingRemovePackageFormProvider
import models.Index
import models.requests.DataRequest
import navigation.ItemsNavigator
import pages.sections.items.ItemsPackagingSectionItems
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.items.ItemPackagingRemovePackageView

import javax.inject.Inject
import scala.concurrent.Future

class ItemPackagingRemovePackageController @Inject()(
                                                      override val messagesApi: MessagesApi,
                                                      override val userAnswersService: UserAnswersService,
                                                      override val userAllowList: UserAllowListAction,
                                                      override val navigator: ItemsNavigator,
                                                      override val auth: AuthAction,
                                                      override val getData: DataRetrievalAction,
                                                      override val requireData: DataRequiredAction,
                                                      formProvider: ItemPackagingRemovePackageFormProvider,
                                                      val controllerComponents: MessagesControllerComponents,
                                                      view: ItemPackagingRemovePackageView
                                                    ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, itemsIdx: Index, packagingIdx: Index): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validatePackagingIndexAsync(itemsIdx, packagingIdx) {
        renderView(Ok, formProvider())(ern, arc, itemsIdx, packagingIdx)
      }
    }

  def onSubmit(ern: String, arc: String, itemsIdx: Index, packagingIdx: Index): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validatePackagingIndexAsync(itemsIdx, packagingIdx) {
        formProvider().bindFromRequest().fold(
          renderView(BadRequest, _)(ern, arc, itemsIdx, packagingIdx),
          handleAnswerRemovalAndRedirect(_, itemsIdx, packagingIdx)(ern, arc)
        )
      }
    }

  private def renderView(status: Status, form: Form[_])(ern: String, arc: String, itemsIdx: Index, packagingIdx: Index)
                        (implicit request: DataRequest[_]): Future[Result] = {
    withItemPackaging(itemsIdx, packagingIdx) { packaging =>
      Future.successful(
        status(view(
          form = form,
          action = routes.ItemPackagingRemovePackageController.onSubmit(ern, arc, itemsIdx, packagingIdx),
          packaging
        ))
      )
    }
  }

  private def handleAnswerRemovalAndRedirect(shouldRemoveItem: Boolean, itemIdx: Index, packageIdx: Index)(ern: String, arc: String)
                                            (implicit request: DataRequest[_]): Future[Result] = {
    if (shouldRemoveItem) {
      val cleansedAnswers = request.userAnswers.remove(ItemsPackagingSectionItems(itemIdx, packageIdx))
      userAnswersService.set(cleansedAnswers).map {
        _ => Redirect(routes.ItemsPackagingIndexController.onPageLoad(ern, arc, itemIdx))
      }
    } else {
      Future(Redirect(routes.ItemsPackagingAddToListController.onPageLoad(ern, arc, itemIdx)))
    }
  }
}
