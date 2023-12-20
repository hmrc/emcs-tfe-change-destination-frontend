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
import forms.sections.journeyType.JourneyTimeHoursFormProvider
import models.Mode
import models.requests.DataRequest
import navigation.JourneyTypeNavigator
import pages.sections.journeyType.{JourneyTimeDaysPage, JourneyTimeHoursPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.journeyType.JourneyTimeHoursView

import javax.inject.Inject
import scala.concurrent.Future

class JourneyTimeHoursController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            override val userAllowList: UserAllowListAction,
                                            override val userAnswersService: UserAnswersService,
                                            override val navigator: JourneyTypeNavigator,
                                            override val auth: AuthAction,
                                            override val getData: DataRetrievalAction,
                                            override val requireData: DataRequiredAction,
                                            formProvider: JourneyTimeHoursFormProvider,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: JourneyTimeHoursView
                                          ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      renderView(Ok, fillForm(JourneyTimeHoursPage, formProvider()), mode)
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        renderView(BadRequest, _, mode),
        amountOfHours => {
          val cleansedAnswers = request.userAnswers.remove(JourneyTimeDaysPage)
          saveAndRedirect(JourneyTimeHoursPage, amountOfHours, cleansedAnswers, mode)
        }
      )
    }

  private def renderView(status: Status, form: Form[_], mode: Mode)(implicit request: DataRequest[_]): Future[Result] = {
    Future.successful(
      status(view(
        form = form,
        mode = mode
      ))
    )
  }
}
