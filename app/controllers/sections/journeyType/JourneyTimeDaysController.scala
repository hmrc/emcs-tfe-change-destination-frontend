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

package controllers.sections.journeyType

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.journeyType.JourneyTimeDaysFormProvider
import models.{Mode, UserAnswers}
import models.requests.DataRequest
import models.sections.journeyType.HowMovementTransported
import models.sections.journeyType.HowMovementTransported._
import navigation.JourneyTypeNavigator
import pages.sections.journeyType.{HowMovementTransportedPage, JourneyTimeDaysPage, JourneyTimeHoursPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.journeyType.JourneyTimeDaysView

import javax.inject.Inject
import scala.concurrent.Future

class JourneyTimeDaysController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           override val userAnswersService: UserAnswersService,
                                           override val navigator: JourneyTypeNavigator,
                                           override val auth: AuthAction,
                                           override val getData: DataRetrievalAction,
                                           override val requireData: DataRequiredAction,
                                           override val withMovement: MovementAction,
                                           formProvider: JourneyTimeDaysFormProvider,
                                           val controllerComponents: MessagesControllerComponents,
                                           view: JourneyTimeDaysView
                                         ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      withAnswerAsync(HowMovementTransportedPage) { transportMode =>
        renderView(Ok, fillForm(JourneyTimeDaysPage, formProvider(HowMovementTransported.transportModeToMaxDays(transportMode))), mode)
      }
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      withAnswerAsync(HowMovementTransportedPage) { transportMode =>
        formProvider(transportModeToMaxDays(transportMode)).bindFromRequest().fold(
          renderView(BadRequest, _, mode),
          amountOfDays => {
            val cleansedAnswers = cleanseAnswers(amountOfDays)
            userAnswersService.set(cleansedAnswers).map { userAnswers =>
              Redirect(navigator.nextPage(JourneyTimeDaysPage, mode, userAnswers))
            }
          }
        )
      }
    }

  private def renderView(status: Status, form: Form[_], mode: Mode)
                        (implicit request: DataRequest[_]): Future[Result] = {
    Future.successful(
      status(view(
        form = form,
        mode = mode
      ))
    )
  }

  private def cleanseAnswers(answer: Int)(implicit request: DataRequest[_]): UserAnswers = {
    if(JourneyTimeDaysPage.getValueFromIE801.contains(answer)) {
      request.userAnswers.remove(JourneyTimeHoursPage).remove(JourneyTimeDaysPage)
    } else {
      request.userAnswers.remove(JourneyTimeHoursPage).set(JourneyTimeDaysPage, answer)
    }
  }
}
