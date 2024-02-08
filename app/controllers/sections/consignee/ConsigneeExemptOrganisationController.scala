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

package controllers.sections.consignee

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.consignee.ConsigneeExemptOrganisationFormProvider
import models.Mode
import models.requests.DataRequest
import navigation.ConsigneeNavigator
import pages.sections.consignee.ConsigneeExemptOrganisationPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{GetMemberStatesService, UserAnswersService}
import views.html.sections.consignee.ConsigneeExemptOrganisationView

import javax.inject.Inject
import scala.concurrent.Future

class ConsigneeExemptOrganisationController @Inject()(override val messagesApi: MessagesApi,
                                                      override val userAnswersService: UserAnswersService,
                                                      override val navigator: ConsigneeNavigator,
                                                      override val auth: AuthAction,
                                                      override val getData: DataRetrievalAction,
                                                      override val requireData: DataRequiredAction,
                                                      override val withMovement: MovementAction,
                                                      override val betaAllowList: BetaAllowListAction,
                                                      formProvider: ConsigneeExemptOrganisationFormProvider,
                                                      val controllerComponents: MessagesControllerComponents,
                                                      view: ConsigneeExemptOrganisationView,
                                                      getMemberStatesService: GetMemberStatesService
                                                     ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      renderView(Ok, fillForm(ConsigneeExemptOrganisationPage, formProvider()), mode)
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      submitAndTrimWhitespaceFromTextarea(ConsigneeExemptOrganisationPage, formProvider)(
        formWithErrors =>
          renderView(BadRequest, formWithErrors, mode)
      )(
        value =>
          saveAndRedirect(ConsigneeExemptOrganisationPage, value, mode)
      )
    }

  private def renderView(status: Status, form: Form[_], mode: Mode)(implicit request: DataRequest[_]): Future[Result] = {
    getMemberStatesService.getMemberStatesSelectItems().map { selectItems =>
      status(view(
        form = form,
        items = selectItems,
        call = controllers.sections.consignee.routes.ConsigneeExemptOrganisationController.onSubmit(request.ern, request.arc, mode)
      ))
    }

  }
}
