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
import forms.sections.items.ItemBulkPackagingChoiceFormProvider
import models.GoodsType
import models.requests.DataRequest
import models.{Index, Mode, UserAnswers}
import navigation.ItemsNavigator
import pages.sections.items._
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.items.ItemBulkPackagingChoiceView

import javax.inject.Inject
import scala.concurrent.Future

class ItemBulkPackagingChoiceController @Inject()(
                                                   override val messagesApi: MessagesApi,
                                                   override val userAnswersService: UserAnswersService,
                                                   override val userAllowList: UserAllowListAction,
                                                   override val navigator: ItemsNavigator,
                                                   override val auth: AuthAction,
                                                   override val getData: DataRetrievalAction,
                                                   override val requireData: DataRequiredAction,
                                                   formProvider: ItemBulkPackagingChoiceFormProvider,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   view: ItemBulkPackagingChoiceView
                                                 ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      validateIndex(idx) {
        withGoodsType(idx) { goodsType =>
          renderView(Ok, fillForm(ItemBulkPackagingChoicePage(idx), formProvider(goodsType)), idx, goodsType, mode)
        }
      }
    }

  def onSubmit(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndexAsync(idx) {
        withGoodsTypeAsync(idx) { goodsType =>
          formProvider(goodsType).bindFromRequest().fold(
            formWithErrors =>
              Future.successful(renderView(BadRequest, formWithErrors, idx, goodsType, mode)),
            value => {
              val newUserAnswers = cleanseUserAnswersIfValueHasChanged(ItemBulkPackagingChoicePage(idx), value, cleanseFunction(idx))
              saveAndRedirect(ItemBulkPackagingChoicePage(idx), value, newUserAnswers, mode)
            }
          )
        }
      }
    }

  private def cleanseFunction(idx: Index)(implicit request: DataRequest[_]): UserAnswers =
    request.userAnswers
      // individual item pages
      .remove(ItemsPackagingSection(idx))
      // bulk pages
      .remove(ItemBulkPackagingSection(idx))
      // wine pages
      .remove(ItemWineSection(idx))

  private def renderView(status: Status, form: Form[_], idx: Index, goodsType: GoodsType, mode: Mode)(implicit request: DataRequest[_]): Result =
    status(view(
      form = form,
      action = routes.ItemBulkPackagingChoiceController.onSubmit(request.ern, request.arc, idx, mode),
      goodsType = goodsType
    ))
}
