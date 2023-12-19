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
import forms.sections.items.ItemSelectPackagingFormProvider
import models.GoodsType
import models.requests.DataRequest
import models.response.referenceData.ItemPackaging._
import models.{Index, Mode}
import navigation.ItemsNavigator
import pages.sections.items.{ItemSelectPackagingPage, ItemsPackagingSection}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import queries.ItemsPackagingCount
import services.{GetPackagingTypesService, UserAnswersService}
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem
import viewmodels.helpers.SelectItemHelper
import views.html.sections.items.ItemSelectPackagingView

import javax.inject.Inject
import scala.concurrent.Future

class ItemSelectPackagingController @Inject()(
                                               override val messagesApi: MessagesApi,
                                               override val userAnswersService: UserAnswersService,
                                               override val navigator: ItemsNavigator,
                                               override val auth: AuthAction,
                                               override val getData: DataRetrievalAction,
                                               override val requireData: DataRequiredAction,
                                               override val userAllowList: UserAllowListAction,
                                               formProvider: ItemSelectPackagingFormProvider,
                                               val controllerComponents: MessagesControllerComponents,
                                               getPackagingTypesService: GetPackagingTypesService,
                                               view: ItemSelectPackagingView
                                             ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, itemsIndex: Index, packagingIdx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validatePackagingIndexAsync(itemsIndex, packagingIdx) {
        withGoodsTypeAsync(itemsIndex) { goodsType =>
          getPackagingTypesService.getItemPackagingTypes(Some(true)).flatMap { nonCountablePackagingTypes =>
            val selectItems = SelectItemHelper.constructSelectItems(
              selectOptions = nonCountablePackagingTypes,
              defaultTextMessageKey = "itemSelectPackaging.select.defaultValue",
              existingAnswer = request.userAnswers.get(ItemSelectPackagingPage(itemsIndex, packagingIdx)).map(_.code)
            )
            renderView(Ok, fillForm(ItemSelectPackagingPage(itemsIndex, packagingIdx), formProvider(goodsType, nonCountablePackagingTypes)),
              itemsIndex, packagingIdx, goodsType, selectItems, mode)
          }
        }
      }
    }

  def onSubmit(ern: String, arc: String, itemsIndex: Index, packagingIdx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validatePackagingIndexAsync(itemsIndex, packagingIdx) {
        withGoodsTypeAsync(itemsIndex) { goodsType =>
          getPackagingTypesService.getItemPackagingTypes(Some(true)).flatMap { nonCountablePackagingTypes =>
            val selectItems = SelectItemHelper.constructSelectItems(
              nonCountablePackagingTypes,
              defaultTextMessageKey = "itemSelectPackaging.select.defaultValue"
            )
            formProvider(goodsType, nonCountablePackagingTypes).bindFromRequest().fold(
              renderView(BadRequest, _, itemsIndex, packagingIdx, goodsType, selectItems, mode),
              saveAndRedirect(ItemSelectPackagingPage(itemsIndex, packagingIdx), _, mode)
            )
          }
        }
      }
    }

  override def validatePackagingIndexAsync(itemsIdx: Index, itemsPackagingIdx: Index)(f: => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    validateIndexForJourneyEntry(ItemsPackagingCount(itemsIdx), itemsPackagingIdx, ItemsPackagingSection(itemsIdx).MAX)(
      onSuccess = f,
      onFailure = Future.successful(
        Redirect(
          controllers.sections.items.routes.ItemsPackagingIndexController.onPageLoad(request.ern, request.arc, itemsIdx)
        )
      )
    )

  private def renderView(status: Status, form: Form[_], itemsIndex: Index, packagingIdx: Index, goodsType: GoodsType, selectItems: Seq[SelectItem], mode: Mode)
                        (implicit request: DataRequest[_]): Future[Result] =
    Future.successful(status(view(
      form = form,
      action = routes.ItemSelectPackagingController.onSubmit(request.ern, request.arc, itemsIndex, packagingIdx, mode),
      selectOptions = selectItems,
      goodsType = goodsType
    )))
}
