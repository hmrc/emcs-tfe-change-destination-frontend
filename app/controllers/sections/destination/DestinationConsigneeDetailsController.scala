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

package controllers.sections.destination

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.destination.DestinationConsigneeDetailsFormProvider
import models.requests.DataRequest
import models.{Mode, NormalMode}
import navigation.DestinationNavigator
import pages.sections.destination.{DestinationAddressPage, DestinationBusinessNamePage, DestinationConsigneeDetailsPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.destination.DestinationConsigneeDetailsView

import javax.inject.Inject
import scala.concurrent.Future

class DestinationConsigneeDetailsController @Inject()(
                                                       override val messagesApi: MessagesApi,
                                                       override val userAnswersService: UserAnswersService,
                                                       override val navigator: DestinationNavigator,
                                                       override val auth: AuthAction,
                                                       override val getData: DataRetrievalAction,
                                                       override val requireData: DataRequiredAction,
                                                       override val withMovement: MovementAction,
                                                       formProvider: DestinationConsigneeDetailsFormProvider,
                                                       val controllerComponents: MessagesControllerComponents,
                                                       view: DestinationConsigneeDetailsView
                                                     ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      renderView(Ok, fillForm(DestinationConsigneeDetailsPage, formProvider()), mode)
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          renderView(BadRequest, formWithErrors, mode),
        value => if (request.userAnswers.get(DestinationConsigneeDetailsPage).contains(value)) {
          Future(Redirect(navigator.nextPage(DestinationConsigneeDetailsPage, mode, request.userAnswers)))
        } else {

          val cleanedUserAnswers = if (!value) request.userAnswers else request.userAnswers
            .remove(DestinationBusinessNamePage)
            .remove(DestinationAddressPage)

          saveAndRedirect(
            page = DestinationConsigneeDetailsPage,
            answer = value,
            currentAnswers = cleanedUserAnswers,
            mode = NormalMode
          )
        }
      )
    }

  def renderView(status: Status, form: Form[_], mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
    Future.successful(status(view(
      form = form,
      mode = mode
    )))


}
