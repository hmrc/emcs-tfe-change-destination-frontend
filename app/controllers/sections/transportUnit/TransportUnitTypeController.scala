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

package controllers.sections.transportUnit

import controllers.actions._
import forms.sections.transportUnit.TransportUnitTypeFormProvider
import models.requests.DataRequest
import models.sections.transportUnit.TransportUnitType
import models.sections.transportUnit.TransportUnitType.FixedTransport
import models.{Index, Mode}
import navigation.TransportUnitNavigator
import pages.sections.transportUnit._
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import queries.TransportUnitsCount
import services.UserAnswersService
import views.html.sections.transportUnit.TransportUnitTypeView

import javax.inject.Inject
import scala.concurrent.Future

class TransportUnitTypeController @Inject()(
                                             override val messagesApi: MessagesApi,
                                             override val userAnswersService: UserAnswersService,
                                             override val userAllowList: UserAllowListAction,
                                             override val navigator: TransportUnitNavigator,
                                             override val auth: AuthAction,
                                             override val getData: DataRetrievalAction,
                                             override val requireData: DataRequiredAction,
                                             override val withMovement: MovementAction,
                                             formProvider: TransportUnitTypeFormProvider,
                                             val controllerComponents: MessagesControllerComponents,
                                             view: TransportUnitTypeView
                                           ) extends BaseTransportUnitNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      validateIndex(idx) {
        renderView(Ok, fillForm(TransportUnitTypePage(idx), formProvider()), idx, mode)
      }
    }

  def onSubmit(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      validateIndex(idx) {
        formProvider().bindFromRequest().fold(
          formWithErrors =>
            renderView(BadRequest, formWithErrors, idx, mode),
          value => {
            val cleansedAnswers = {
              if (value == FixedTransport) {
                request.userAnswers
                  .remove(TransportUnitGiveMoreInformationChoicePage(idx))
                  .remove(TransportUnitIdentityPage(idx))
                  .remove(TransportSealChoicePage(idx))
                  .remove(TransportSealTypePage(idx))
                  .remove(TransportUnitGiveMoreInformationPage(idx))
              } else {
                request.userAnswers
              }
            }
            saveAndRedirect(TransportUnitTypePage(idx), value, cleansedAnswers, mode)
          }
        )
      }
    }

  override def validateIndex(idx: Index)(f: => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    validateIndexForJourneyEntry(TransportUnitsCount, idx, TransportUnitsSection.MAX)(
      onSuccess = f,
      onFailure = Future.successful(
        Redirect(
          controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(request.ern, request.arc)
        )
      )
    )

  private def renderView(status: Status, form: Form[TransportUnitType], idx: Index, mode: Mode)
                        (implicit request: DataRequest[_]): Future[Result] = {
    Future.successful(
      status(view(
        form = form,
        idx = idx,
        mode = mode
      ))
    )
  }
}
