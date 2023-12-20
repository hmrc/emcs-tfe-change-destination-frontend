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

package controllers.sections.exportInformation

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.exportInformation.ExportCustomsOfficeFormProvider
import models.Mode
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.ExportWithCustomsDeclarationLodgedInTheEu
import navigation.ExportInformationNavigator
import pages.sections.exportInformation.ExportCustomsOfficePage
import pages.sections.info.DestinationTypePage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.exportInformation.ExportCustomsOfficeView

import javax.inject.Inject
import scala.concurrent.Future

class ExportCustomsOfficeController @Inject()(
                                               override val messagesApi: MessagesApi,
                                               override val userAnswersService: UserAnswersService,
                                               override val navigator: ExportInformationNavigator,
                                               override val auth: AuthAction,
                                               override val getData: DataRetrievalAction,
                                               override val requireData: DataRequiredAction,
                                               override val userAllowList: UserAllowListAction,
                                               formProvider: ExportCustomsOfficeFormProvider,
                                               val controllerComponents: MessagesControllerComponents,
                                               view: ExportCustomsOfficeView
                                             ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      renderView(Ok, fillForm(ExportCustomsOfficePage, formProvider()), mode)
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithError => Future.successful(renderView(BadRequest, formWithError, mode)),
        saveAndRedirect(ExportCustomsOfficePage, _, mode)
      )
    }

  private def renderView(status: Status, form: Form[_], mode: Mode)(implicit request: DataRequest[_]): Result = {
    withAnswer(DestinationTypePage) { destinationType =>
      status(view(
        form = form,
        action = routes.ExportCustomsOfficeController.onSubmit(request.ern, request.arc, mode),
        euExport = destinationType == ExportWithCustomsDeclarationLodgedInTheEu
      ))
    }
  }

}
