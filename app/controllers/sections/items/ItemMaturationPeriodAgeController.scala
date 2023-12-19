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
import forms.sections.items.ItemMaturationPeriodAgeFormProvider
import forms.sections.items.ItemMaturationPeriodAgeFormProvider._
import models.GoodsType
import models.requests.DataRequest
import models.sections.items.ItemMaturationPeriodAgeModel
import models.{Index, Mode}
import navigation.ItemsNavigator
import pages.sections.items.ItemMaturationPeriodAgePage
import play.api.data.Form
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.items.ItemMaturationPeriodAgeView

import javax.inject.Inject
import scala.concurrent.Future

class ItemMaturationPeriodAgeController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         override val userAnswersService: UserAnswersService,
                                         override val userAllowList: UserAllowListAction,
                                         override val navigator: ItemsNavigator,
                                         override val auth: AuthAction,
                                         override val getData: DataRetrievalAction,
                                         override val requireData: DataRequiredAction,
                                         formProvider: ItemMaturationPeriodAgeFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: ItemMaturationPeriodAgeView
                                       ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndexAsync(idx) {
        withGoodsTypeAsync(idx) { goodsType =>
          renderView(Ok, fillForm(ItemMaturationPeriodAgePage(idx), formProvider(goodsType)), idx, mode, goodsType)
        }
      }
    }

  def onSubmit(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndexAsync(idx) {
        withGoodsTypeAsync(idx) { goodsType =>
          formProvider(goodsType).bindFromRequest().fold(
            renderView(BadRequest, _, idx, mode, goodsType),
            handleSubmittedForm(_, idx, mode, goodsType)
          )
        }
      }
    }

  private def renderView(status: Status, form: Form[_], idx: Index, mode: Mode, goodsType: GoodsType)(implicit request: DataRequest[_]): Future[Result] =
      Future.successful(status(view(
        form = form,
        action = routes.ItemMaturationPeriodAgeController.onSubmit(request.ern, request.arc, idx, mode),
        goodsType = goodsType
      )))

  private def handleSubmittedForm(maturationPeriodAgeModel: ItemMaturationPeriodAgeModel, idx: Index, mode: Mode, goodsType: GoodsType)
                                 (implicit request: DataRequest[_], messages: Messages): Future[Result] =
    if(maturationPeriodAgeModel.hasMaturationPeriodAge && maturationPeriodAgeModel.maturationPeriodAge.isEmpty) {
      renderView(
        status = BadRequest,
        form =
          formProvider(goodsType)(messages)
            .fill(maturationPeriodAgeModel)
            .withError(maturationPeriodAgeField, messages(maturationPeriodAgeRequired)),
        idx = idx,
        mode = mode,
        goodsType
      )
    } else {
      saveAndRedirect(ItemMaturationPeriodAgePage(idx), maturationPeriodAgeModel, mode)
    }
}
