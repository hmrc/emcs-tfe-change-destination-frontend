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
import forms.sections.items.ItemDegreesPlatoFormProvider
import forms.sections.items.ItemDegreesPlatoFormProvider._
import models.requests.DataRequest
import models.sections.items.ItemDegreesPlatoModel
import models.{Index, Mode}
import navigation.ItemsNavigator
import pages.sections.items.ItemDegreesPlatoPage
import play.api.data.Form
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.items.ItemDegreesPlatoView

import javax.inject.Inject
import scala.concurrent.Future

class ItemDegreesPlatoController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            override val userAnswersService: UserAnswersService,
                                            override val userAllowList: UserAllowListAction,
                                            override val navigator: ItemsNavigator,
                                            override val auth: AuthAction,
                                            override val getData: DataRetrievalAction,
                                            override val requireData: DataRequiredAction,
                                            formProvider: ItemDegreesPlatoFormProvider,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: ItemDegreesPlatoView
                                          ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndexAsync(idx) {
        renderView(Ok, fillForm(ItemDegreesPlatoPage(idx), formProvider()), idx, mode)
      }
    }

  def onSubmit(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndexAsync(idx) {
        formProvider().bindFromRequest().fold(
          renderView(BadRequest, _, idx, mode),
          handleSubmittedForm(_, idx, mode)
        )
      }
    }

  private def renderView(status: Status, form: Form[_], idx: Index, mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
    withGoodsTypeAsync(idx) { goodsType =>
      Future.successful(status(view(
        form = form,
        action = routes.ItemDegreesPlatoController.onSubmit(request.ern, request.arc, idx, mode),
        goodsType = goodsType
      )))
    }

  private def handleSubmittedForm(degreesPlatoModel: ItemDegreesPlatoModel, idx: Index, mode: Mode)(implicit request: DataRequest[_], messages: Messages) = {

    if (degreesPlatoModel.hasDegreesPlato && degreesPlatoModel.degreesPlato.isEmpty) {
      renderView(
        status = BadRequest,
        form =
          formProvider()
            .fill(degreesPlatoModel)
            .withError(degreesPlatoField, messages(requiredErrorKey)),
        idx = idx,
        mode = mode
      )
    } else {
      saveAndRedirect(ItemDegreesPlatoPage(idx), degreesPlatoModel, mode)
    }
  }
}
