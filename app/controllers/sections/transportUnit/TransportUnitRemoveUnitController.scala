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
import forms.sections.transportUnit.TransportUnitRemoveUnitFormProvider
import models.Index
import models.requests.DataRequest
import navigation.TransportUnitNavigator
import pages.sections.transportUnit.TransportUnitSection
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.transportUnit.TransportUnitRemoveUnitView

import javax.inject.Inject
import scala.concurrent.Future

class TransportUnitRemoveUnitController @Inject()(
                                                   override val messagesApi: MessagesApi,
                                                   override val userAnswersService: UserAnswersService,
                                                   override val userAllowList: UserAllowListAction,
                                                   override val navigator: TransportUnitNavigator,
                                                   override val auth: AuthAction,
                                                   override val getData: DataRetrievalAction,
                                                   override val requireData: DataRequiredAction,
                                                   formProvider: TransportUnitRemoveUnitFormProvider,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   view: TransportUnitRemoveUnitView
                                                 ) extends BaseTransportUnitNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Index): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndex(idx) {
        renderView(Ok, formProvider(), idx)
      }
    }

  def onSubmit(ern: String, arc: String, idx: Index): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndex(idx) {
        formProvider().bindFromRequest().fold(
          renderView(BadRequest, _, idx),
          handleAnswerRemovalAndRedirect(_, idx)(ern, arc)
        )
      }
    }

  private def renderView(status: Status, form: Form[_], idx: Index)
                        (implicit request: DataRequest[_]): Future[Result] = {
    Future.successful(
      status(view(
        form = form,
        indexOfTransportUnit = idx
      ))
    )
  }

  private def handleAnswerRemovalAndRedirect(shouldRemoveTransportUnit: Boolean, index: Index)(ern: String, arc: String)
                                            (implicit request: DataRequest[_]): Future[Result] = {
    if (shouldRemoveTransportUnit) {
      val cleansedAnswers = request.userAnswers.remove(TransportUnitSection(index))
      userAnswersService.set(cleansedAnswers).map {
        _ => Redirect(controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(ern, arc))
      }
    } else {
      Future(Redirect(controllers.sections.transportUnit.routes.TransportUnitsAddToListController.onPageLoad(ern, arc)))
    }
  }
}
