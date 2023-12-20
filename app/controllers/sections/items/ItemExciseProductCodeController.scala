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
import forms.sections.items.ItemExciseProductCodeFormProvider
import models.requests.DataRequest
import models.{Index, Mode}
import navigation.ItemsNavigator
import pages.sections.items.{ItemExciseProductCodePage, ItemsSectionItem, ItemsSectionItems}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import queries.ItemsCount
import services.{GetExciseProductCodesService, UserAnswersService}
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem
import viewmodels.helpers.SelectItemHelper
import views.html.sections.items.ItemExciseProductCodeView

import javax.inject.Inject
import scala.concurrent.Future

class ItemExciseProductCodeController @Inject()(
                                                 override val messagesApi: MessagesApi,
                                                 override val userAnswersService: UserAnswersService,
                                                 override val navigator: ItemsNavigator,
                                                 override val auth: AuthAction,
                                                 override val getData: DataRetrievalAction,
                                                 override val requireData: DataRequiredAction,
                                                 override val userAllowList: UserAllowListAction,
                                                 formProvider: ItemExciseProductCodeFormProvider,
                                                 val controllerComponents: MessagesControllerComponents,
                                                 exciseProductCodesService: GetExciseProductCodesService,
                                                 view: ItemExciseProductCodeView
                                               ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndexAsync(idx) {
        exciseProductCodesService.getExciseProductCodes().flatMap {
          exciseProductCodes =>
            val selectItems = SelectItemHelper.constructSelectItems(
              selectOptions = exciseProductCodes,
              defaultTextMessageKey = "itemExciseProductCode.select.defaultValue",
              existingAnswer = request.userAnswers.get(ItemExciseProductCodePage(idx))
            )
            renderView(Ok, formProvider(exciseProductCodes), idx, selectItems, mode)
        }
      }
    }

  def onSubmit(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndexAsync(idx) {
        exciseProductCodesService.getExciseProductCodes().flatMap {
          exciseProductCodes => {
            formProvider(exciseProductCodes).bindFromRequest().fold(
              formWithErrors => {
                val selectItems = SelectItemHelper.constructSelectItems(
                  exciseProductCodes,
                  defaultTextMessageKey = "itemExciseProductCode.select.defaultValue"
                )
                renderView(BadRequest, formWithErrors, idx, selectItems, mode)
              },
              value => {
                val updatedUserAnswers = cleanseUserAnswersIfValueHasChanged(
                  page = ItemExciseProductCodePage(idx),
                  newAnswer = value,
                  cleansingFunction = request.userAnswers.resetIndexedSection(ItemsSectionItem(idx), idx)
                )
                saveAndRedirect(ItemExciseProductCodePage(idx), value, updatedUserAnswers, mode)
              }
            )
          }
        }
      }
    }

  override def validateIndexAsync(idx: Index)(f: => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    validateIndexForJourneyEntry(ItemsCount, idx, ItemsSectionItems.MAX)(
      onSuccess = f,
      onFailure = Future.successful(
        Redirect(
          controllers.sections.items.routes.ItemsIndexController.onPageLoad(request.ern, request.arc)
        )
      )
    )

  private def renderView(status: Status, form: Form[_], idx: Index, selectItems: Seq[SelectItem], mode: Mode)
                        (implicit request: DataRequest[_]): Future[Result] = {
    Future.successful(status(view(
      form = form,
      action = routes.ItemExciseProductCodeController.onSubmit(request.ern, request.arc, idx, mode),
      selectOptions = selectItems
    )))
  }
}
