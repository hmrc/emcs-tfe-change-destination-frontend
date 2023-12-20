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
import forms.sections.items.ItemDensityFormProvider
import models.{Index, Mode}
import navigation.ItemsNavigator
import pages.sections.items.ItemDensityPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.sections.items.ItemDensityView

import javax.inject.Inject
import scala.concurrent.Future

class ItemDensityController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val navigator: ItemsNavigator,
                                       override val auth: AuthAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       override val userAllowList: UserAllowListAction,
                                       formProvider: ItemDensityFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: ItemDensityView
                                     ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      withGoodsType(idx) {
        goodsType =>
          Ok(view(fillForm(ItemDensityPage(idx), formProvider(goodsType)), routes.ItemDensityController.onSubmit(ern, arc, idx, mode), goodsType))
      }
    }

  def onSubmit(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      withGoodsTypeAsync(idx) {
        goodsType =>
          formProvider(goodsType).bindFromRequest().fold(
            formWithErrors =>
              Future.successful(BadRequest(view(formWithErrors, routes.ItemDensityController.onSubmit(ern, arc, idx, mode), goodsType))),
            value =>
              saveAndRedirect(ItemDensityPage(idx), value, mode)
          )
      }
    }
}
