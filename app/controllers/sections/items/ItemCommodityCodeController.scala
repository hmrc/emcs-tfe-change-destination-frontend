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
import forms.sections.items.ItemCommodityCodeFormProvider
import models.response.referenceData.CnCodeInformation
import models.{GoodsType, Index, Mode}
import navigation.ItemsNavigator
import pages.sections.items.{ItemCommodityCodePage, ItemExciseProductCodePage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{GetCommodityCodesService, UserAnswersService}
import views.html.sections.items.ItemCommodityCodeView

import javax.inject.Inject
import scala.concurrent.Future

class ItemCommodityCodeController @Inject()(
                                             override val messagesApi: MessagesApi,
                                             override val userAnswersService: UserAnswersService,
                                             override val navigator: ItemsNavigator,
                                             override val auth: AuthAction,
                                             override val getData: DataRetrievalAction,
                                             override val requireData: DataRequiredAction,
                                             override val userAllowList: UserAllowListAction,
                                             getCommodityCodesService: GetCommodityCodesService,
                                             formProvider: ItemCommodityCodeFormProvider,
                                             val controllerComponents: MessagesControllerComponents,
                                             view: ItemCommodityCodeView
                                           ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      withAnswerAsync(
        ItemExciseProductCodePage(idx),
        redirectRoute = routes.ItemsIndexController.onPageLoad(request.ern, request.arc)
      ) {
        itemExciseProductCode =>
          getCommodityCodesService.getCommodityCodes(itemExciseProductCode).flatMap {
            case Nil =>
              saveAndRedirect(ItemCommodityCodePage(idx), CnCodeInformation.defaultCnCode, mode)
            case singleCommodityCode :: Nil =>
              saveAndRedirect(ItemCommodityCodePage(idx), singleCommodityCode.cnCode, mode)
            case commodityCodes =>
              Future.successful(Ok(
                view(
                  form = fillForm(ItemCommodityCodePage(idx), formProvider()),
                  action = routes.ItemCommodityCodeController.onSubmit(request.ern, request.arc, idx, mode),
                  goodsType = GoodsType(itemExciseProductCode),
                  commodityCodes
                )
              ))
          }
      }
    }

  def onSubmit(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          withAnswerAsync(
            ItemExciseProductCodePage(idx),
            redirectRoute = routes.ItemsIndexController.onPageLoad(request.ern, request.arc)
          ) {
            itemExciseProductCode =>
              getCommodityCodesService.getCommodityCodes(itemExciseProductCode).map {
                commodityCodes =>
                  BadRequest(
                    view(
                      form = formWithErrors,
                      action = routes.ItemCommodityCodeController.onSubmit(request.ern, request.arc, idx, mode),
                      goodsType = GoodsType(itemExciseProductCode),
                      commodityCodes
                    )
                  )
              }
          },
        value =>
          saveAndRedirect(ItemCommodityCodePage(idx), value, mode)
      )
    }
}
