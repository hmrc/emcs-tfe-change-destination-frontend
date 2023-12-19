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
import forms.sections.items.ItemImportedWineChoiceFormProvider
import models.requests.DataRequest
import models.{Index, Mode, UserAnswers}
import navigation.ItemsNavigator
import pages.sections.items.{ItemImportedWineFromEuChoicePage, ItemWineGrowingZonePage, ItemWineOriginPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.items.ItemImportedWineChoiceView

import javax.inject.Inject
import scala.concurrent.Future

class ItemImportedWineChoiceController @Inject()(
                                                  override val messagesApi: MessagesApi,
                                                  override val userAnswersService: UserAnswersService,
                                                  override val navigator: ItemsNavigator,
                                                  override val auth: AuthAction,
                                                  override val getData: DataRetrievalAction,
                                                  override val requireData: DataRequiredAction,
                                                  override val userAllowList: UserAllowListAction,
                                                  formProvider: ItemImportedWineChoiceFormProvider,
                                                  val controllerComponents: MessagesControllerComponents,
                                                  view: ItemImportedWineChoiceView
                                                ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndexAsync(idx) {
        renderView(Ok, fillForm(ItemImportedWineFromEuChoicePage(idx), formProvider()), idx, mode)
      }
    }

  def onSubmit(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndexAsync(idx) {
        formProvider().bindFromRequest().fold(
          renderView(BadRequest, _, idx, mode),
          answer => saveAndRedirect(ItemImportedWineFromEuChoicePage(idx), answer, cleanseFunction(idx, answer), mode)
        )
      }
    }

  private def cleanseFunction(idx: Index, wineInEu: Boolean)(implicit request: DataRequest[_]): UserAnswers = {
    // if wine in eu, clear third country of origin
    // if not, clear wine growing zone
    if(wineInEu) {
      request.userAnswers.remove(ItemWineOriginPage(idx))
    } else {
      request.userAnswers.remove(ItemWineGrowingZonePage(idx))
    }
  }

  private def renderView(status: Status, form: Form[_], idx: Index, mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
    Future.successful(status(view(
      form = form,
      action = routes.ItemImportedWineChoiceController.onSubmit(request.ern, request.arc, idx, mode)
    )))

}
