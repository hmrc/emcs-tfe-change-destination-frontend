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
import forms.sections.items.ItemWineOperationsChoiceFormProvider
import models.requests.DataRequest
import models.response.referenceData.WineOperations
import models.{Index, Mode}
import navigation.ItemsNavigator
import pages.sections.items.ItemWineOperationsChoicePage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{GetWineOperationsService, UserAnswersService}
import views.html.sections.items.ItemWineOperationsChoiceView

import javax.inject.Inject
import scala.concurrent.Future

class ItemWineOperationsChoiceController @Inject()(
                                                    override val messagesApi: MessagesApi,
                                                    override val userAnswersService: UserAnswersService,
                                                    override val userAllowList: UserAllowListAction,
                                                    override val navigator: ItemsNavigator,
                                                    override val auth: AuthAction,
                                                    override val getData: DataRetrievalAction,
                                                    override val requireData: DataRequiredAction,
                                                    formProvider: ItemWineOperationsChoiceFormProvider,
                                                    getWineOperationsService: GetWineOperationsService,
                                                    val controllerComponents: MessagesControllerComponents,
                                                    view: ItemWineOperationsChoiceView
                                                  ) extends BaseItemsNavigationController with AuthActionHelper {

  private def form(wineOperations: Seq[WineOperations]): Form[Set[WineOperations]] = formProvider[WineOperations](wineOperations)

  def onPageLoad(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndexAsync(idx) {
        getWineOperationsService.getWineOperations().flatMap { wineOperations =>
          renderView(Ok, fillForm(ItemWineOperationsChoicePage(idx), form(wineOperations)), idx, wineOperations, mode)
        }
      }
    }

  def onSubmit(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndexAsync(idx) {
        getWineOperationsService.getWineOperations().flatMap { wineOperations =>
          form(wineOperations).bindFromRequest().fold(
            renderView(BadRequest, _, idx, wineOperations, mode),
            (values: Set[WineOperations]) =>
              saveAndRedirect(ItemWineOperationsChoicePage(idx), values, mode)
          )
        }
      }
    }

  private def renderView(
                          status: Status,
                          form: Form[_],
                          idx: Index,
                          wineOperations: Seq[WineOperations],
                          mode: Mode
                        )(implicit request: DataRequest[_]): Future[Result] =
    Future.successful(
      status(
        view(
          form = form,
          action = routes.ItemWineOperationsChoiceController.onSubmit(request.ern, request.arc, idx, mode),
          values = wineOperations
        )
      )
    )

}
