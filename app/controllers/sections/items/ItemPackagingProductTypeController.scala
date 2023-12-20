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
import forms.sections.items.ItemPackagingProductTypeFormProvider
import models.requests.DataRequest
import models.{Index, Mode}
import navigation.ItemsNavigator
import pages.sections.items.{ItemPackagingProductTypePage, ItemPackagingShippingMarksPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.items.ItemPackagingProductTypeView

import javax.inject.Inject
import scala.concurrent.Future

class ItemPackagingProductTypeController @Inject()(
                                                    override val messagesApi: MessagesApi,
                                                    override val userAnswersService: UserAnswersService,
                                                    override val userAllowList: UserAllowListAction,
                                                    override val navigator: ItemsNavigator,
                                                    override val auth: AuthAction,
                                                    override val getData: DataRetrievalAction,
                                                    override val requireData: DataRequiredAction,
                                                    formProvider: ItemPackagingProductTypeFormProvider,
                                                    val controllerComponents: MessagesControllerComponents,
                                                    view: ItemPackagingProductTypeView
                                                  ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, itemsIdx: Index, packagingIdx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validatePackagingIndexAsync(itemsIdx, packagingIdx) {
        renderView(Ok, fillForm(ItemPackagingProductTypePage(itemsIdx, packagingIdx), formProvider()), itemsIdx, packagingIdx, mode)
      }
    }

  def onSubmit(ern: String, arc: String, itemsIdx: Index, packagingIdx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validatePackagingIndexAsync(itemsIdx, packagingIdx) {
        formProvider().bindFromRequest().fold(
          renderView(BadRequest, _, itemsIdx, packagingIdx, mode),
          cleanseSaveAndRedirect(_, itemsIdx, packagingIdx, mode)
        )
      }
    }

  private def cleanseSaveAndRedirect(hasOneProductType: Boolean, itemIdx: Index, packageIdx: Index, mode: Mode)
                                    (implicit request: DataRequest[_]): Future[Result] = {
    val cleansedAnswers = if (!hasOneProductType) request.userAnswers else {
      request.userAnswers.remove(ItemPackagingShippingMarksPage(itemIdx, packageIdx))
    }
    saveAndRedirect(ItemPackagingProductTypePage(itemIdx, packageIdx), hasOneProductType, cleansedAnswers, mode)
  }

  def renderView(status: Status, form: Form[_], itemsIdx: Index, packagingIdx: Index, mode: Mode)(implicit request: DataRequest[_]): Future[Result] = {
    withItemPackaging(itemsIdx, packagingIdx) { description =>
      Future.successful(status(view(
        form = form,
        description = description,
        onSubmitAction = routes.ItemPackagingProductTypeController.onSubmit(request.ern, request.arc, itemsIdx, packagingIdx, mode)
      )))
    }
  }


}
