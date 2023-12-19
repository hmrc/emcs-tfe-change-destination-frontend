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
import forms.sections.items.ItemPackagingSealChoiceFormProvider
import models.requests.DataRequest
import models.{Index, Mode}
import navigation.ItemsNavigator
import pages.sections.items.{ItemPackagingSealChoicePage, ItemPackagingSealTypePage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc._
import services.UserAnswersService
import views.html.sections.items.ItemPackagingSealChoiceView

import javax.inject.Inject
import scala.concurrent.Future

class ItemPackagingSealChoiceController @Inject()(
                                                   override val messagesApi: MessagesApi,
                                                   override val userAnswersService: UserAnswersService,
                                                   override val userAllowList: UserAllowListAction,
                                                   override val navigator: ItemsNavigator,
                                                   override val auth: AuthAction,
                                                   override val getData: DataRetrievalAction,
                                                   override val requireData: DataRequiredAction,
                                                   formProvider: ItemPackagingSealChoiceFormProvider,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   view: ItemPackagingSealChoiceView
                                                 ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, itemIdx: Index, packagingIdx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validatePackagingIndexAsync(itemIdx, packagingIdx) {
        withItemPackaging(itemIdx, packagingIdx) { description =>
          renderView(Ok, fillForm(ItemPackagingSealChoicePage(itemIdx, packagingIdx), formProvider()), itemIdx, packagingIdx, mode, description)
        }
      }
    }

  def onSubmit(ern: String, arc: String, itemIdx: Index, packagingIdx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validatePackagingIndexAsync(itemIdx, packagingIdx) {
        withItemPackaging(itemIdx, packagingIdx) { description =>
          formProvider().bindFromRequest().fold(
            renderView(BadRequest, _, itemIdx, packagingIdx, mode, description),
            cleanseSaveAndRedirect(_, itemIdx, packagingIdx, mode)
          )
        }
      }
    }

  private def cleanseSaveAndRedirect(hasSeal: Boolean, itemIdx: Index, packageIdx: Index, mode: Mode)(implicit request: DataRequest[_]): Future[Result] = {
    val cleansedAnswers = if(hasSeal) request.userAnswers else {
      request.userAnswers.remove(ItemPackagingSealTypePage(itemIdx, packageIdx))
    }
    saveAndRedirect(ItemPackagingSealChoicePage(itemIdx, packageIdx), hasSeal, cleansedAnswers, mode)
  }

  private def renderView(status: Status, form: Form[_], itemsIndex: Index, packagingIndex: Index, mode: Mode,
                         description: String)(implicit request: DataRequest[_]): Future[Result] =
    Future.successful(status(view(
      form = form,
      action = controllers.sections.items.routes.ItemPackagingSealChoiceController.onSubmit(request.ern, request.arc, itemsIndex, packagingIndex, mode),
      packagingDescription = description
    )))
}
