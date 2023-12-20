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
import forms.sections.items.ItemPackagingShippingMarksFormProvider
import models.requests.DataRequest
import models.{Index, Mode}
import navigation.ItemsNavigator
import pages.sections.items.{ItemPackagingProductTypePage, ItemPackagingShippingMarksPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.items.ItemPackagingShippingMarksView

import javax.inject.Inject
import scala.concurrent.Future

class ItemPackagingShippingMarksController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val userAllowList: UserAllowListAction,
                                       override val navigator: ItemsNavigator,
                                       override val auth: AuthAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       formProvider: ItemPackagingShippingMarksFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: ItemPackagingShippingMarksView
                                     ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, itemsIdx: Index, packagingIdx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validatePackagingIndexAsync(itemsIdx, packagingIdx) {
        renderView(Ok, fillForm(ItemPackagingShippingMarksPage(itemsIdx, packagingIdx), formProvider()), itemsIdx, packagingIdx, mode)
      }
    }

  def onSubmit(ern: String, arc: String, itemsIdx: Index, packagingIdx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validatePackagingIndexAsync(itemsIdx, packagingIdx) {
        formProvider().bindFromRequest().fold(
          renderView(BadRequest, _, itemsIdx, packagingIdx, mode),
          saveAndRedirect(ItemPackagingShippingMarksPage(itemsIdx, packagingIdx), _, mode)
        )
      }
    }

  def onNoShippingMarks(ern: String, arc: String, itemsIndex: Index, packagingIdx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validatePackagingIndexAsync(itemsIndex, packagingIdx) {
        val cleansedAnswers = request.userAnswers
          .set(ItemPackagingProductTypePage(itemsIndex, packagingIdx), true)
          .remove(ItemPackagingShippingMarksPage(itemsIndex, packagingIdx))
        userAnswersService.set(cleansedAnswers).map { savedAnswers =>
          Redirect(navigator.nextPage(ItemPackagingShippingMarksPage(itemsIndex, packagingIdx), mode, savedAnswers))
        }
      }
    }


  private def renderView(status: Status, form: Form[_], itemsIndex: Index, packagingIdx: Index, mode: Mode)
                        (implicit request: DataRequest[_]): Future[Result] = {
    withItemPackaging(itemsIndex, packagingIdx) { packagingDescription =>
      Future.successful(status(view(
        form = form,
        action = routes.ItemPackagingShippingMarksController.onSubmit(request.ern, request.arc, itemsIndex, packagingIdx, mode),
        packagingDescription = packagingDescription,
        skipLink = routes.ItemPackagingShippingMarksController.onNoShippingMarks(request.ern, request.arc, itemsIndex, packagingIdx, mode)
      )))
    }
  }
}
