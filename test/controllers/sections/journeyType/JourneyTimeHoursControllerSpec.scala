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

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import forms.sections.journeyType.JourneyTimeHoursFormProvider
import mocks.services.MockUserAnswersService
import models.response.emcsTfe.GetMovementResponse
import models.sections.journeyType.HowMovementTransported
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeJourneyTypeNavigator
import pages.sections.journeyType.{HowMovementTransportedPage, JourneyTimeDaysPage, JourneyTimeHoursPage}
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.journeyType.JourneyTimeHoursView

import scala.concurrent.Future

class JourneyTimeHoursControllerSpec extends SpecBase with MockUserAnswersService {

  val validAnswer = 1

  lazy val formProvider: JourneyTimeHoursFormProvider = new JourneyTimeHoursFormProvider()
  lazy val form: Form[Int] = formProvider()
  lazy val view: JourneyTimeHoursView = app.injector.instanceOf[JourneyTimeHoursView]

  class Test(val userAnswers: Option[UserAnswers], movementResponse: GetMovementResponse = maxGetMovementResponse) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new JourneyTimeHoursController(
      messagesApi,
      fakeBetaAllowListAction,
      mockUserAnswersService,
      new FakeJourneyTypeNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(movementResponse),
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "JourneyTimeHours Controller" - {

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      emptyUserAnswers.set(JourneyTimeHoursPage, validAnswer)
    )) {

      val result = controller.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(validAnswer), NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Test(Some(emptyUserAnswers)) {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", validAnswer.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must remove both the JourneyTimeHoursPage and JourneyTimeDaysPage answers when the user answer is the same as what is in 801" in new Test(Some(
      emptyUserAnswers
        .set(HowMovementTransportedPage, HowMovementTransported.AirTransport)
        .set(JourneyTimeHoursPage, 1)
        .set(JourneyTimeDaysPage, 3)
    ),
      movementResponse = maxGetMovementResponse.copy(journeyTime = "1 hours")
    ) {

      MockUserAnswersService.set(
        emptyUserAnswers
          .set(HowMovementTransportedPage, HowMovementTransported.AirTransport)
      ).returns(Future.successful(emptyUserAnswers))

      val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", validAnswer.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must remove only the JourneyTimeDaysPage when the answer is different to the 801 value" in new Test(Some(
      emptyUserAnswers
        .set(HowMovementTransportedPage, HowMovementTransported.AirTransport)
        .set(JourneyTimeHoursPage, 2)
        .set(JourneyTimeDaysPage, 3)
    ),
      movementResponse = maxGetMovementResponse.copy(journeyTime = "1 hours")
    ) {

      MockUserAnswersService.set(
        emptyUserAnswers
          .set(HowMovementTransportedPage, HowMovementTransported.AirTransport)
          .set(JourneyTimeHoursPage, 2)
      ).returns(Future.successful(emptyUserAnswers))

      val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", "2")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must remove only the JourneyTimeDaysPage when no JourneyTimeHoursPage exists" in new Test(Some(
      emptyUserAnswers
        .set(HowMovementTransportedPage, HowMovementTransported.AirTransport)
        .set(JourneyTimeDaysPage, 3)
    ),
      movementResponse = maxGetMovementResponse.copy(journeyTime = "1 days")
    ) {

      MockUserAnswersService.set(
        emptyUserAnswers
          .set(HowMovementTransportedPage, HowMovementTransported.AirTransport)
          .set(JourneyTimeHoursPage, 2)
      ).returns(Future.successful(emptyUserAnswers))

      val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", "2")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(emptyUserAnswers)) {
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", validAnswer.toString)))

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
