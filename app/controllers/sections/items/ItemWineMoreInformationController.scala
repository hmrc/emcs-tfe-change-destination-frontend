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
import forms.sections.items.ItemWineMoreInformationFormProvider
import models.requests.DataRequest
import models.{Index, Mode, UserAnswers}
import navigation.ItemsNavigator
import pages.sections.items.{ItemWineMoreInformationChoicePage, ItemWineMoreInformationPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import utils.JsonOptionFormatter
import views.html.sections.items.ItemWineMoreInformationView

import javax.inject.Inject
import scala.concurrent.Future

class ItemWineMoreInformationController @Inject()(
                                                   override val messagesApi: MessagesApi,
                                                   override val userAnswersService: UserAnswersService,
                                                   override val userAllowList: UserAllowListAction,
                                                   override val navigator: ItemsNavigator,
                                                   override val auth: AuthAction,
                                                   override val getData: DataRetrievalAction,
                                                   override val requireData: DataRequiredAction,
                                                   formProvider: ItemWineMoreInformationFormProvider,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   view: ItemWineMoreInformationView
                                                 ) extends BaseItemsNavigationController with AuthActionHelper with JsonOptionFormatter {

  def onPageLoad(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndexAsync(idx) {
        renderView(Ok, fillForm(ItemWineMoreInformationPage(idx), formProvider()), idx, mode)
      }
    }

  def onSubmit(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndexAsync(idx) {
        submitAndTrimWhitespaceFromTextarea(ItemWineMoreInformationPage(idx), formProvider)(
          renderView(BadRequest, _, idx, mode))(
          answer => saveAndRedirect(ItemWineMoreInformationPage(idx), answer, cleanseFunction(idx, answer), mode)
        )
      }
    }


  private def renderView(status: Status, form: Form[_], idx: Index, mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
    Future.successful(status(view(
      form = form,
      action = routes.ItemWineMoreInformationController.onSubmit(request.ern, request.arc, idx, mode)
    )))

  private def cleanseFunction(idx: Index, info: Option[String])(implicit request: DataRequest[_]): UserAnswers =
    request.userAnswers.set(ItemWineMoreInformationChoicePage(idx), info.isDefined)
}
