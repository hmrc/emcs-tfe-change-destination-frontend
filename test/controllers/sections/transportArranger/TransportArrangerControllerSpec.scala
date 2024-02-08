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

package controllers.sections.transportArranger

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import controllers.routes
import forms.sections.transportArranger.TransportArrangerFormProvider
import mocks.services.MockUserAnswersService
import models.sections.transportArranger.TransportArranger
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeTransportArrangerNavigator
import pages.sections.transportArranger.TransportArrangerPage
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.transportArranger.TransportArrangerView

import scala.concurrent.Future

class TransportArrangerControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: TransportArrangerFormProvider = new TransportArrangerFormProvider()
  lazy val form: Form[TransportArranger] = formProvider()
  lazy val view: TransportArrangerView = app.injector.instanceOf[TransportArrangerView]

  class Test(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new TransportArrangerController(
      messagesApi,
      mockUserAnswersService,
      new FakeTransportArrangerNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse.copy(deliveryPlaceCustomsOfficeReferenceNumber = None)),
      fakeBetaAllowListAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "TransportArranger Controller" - {

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      emptyUserAnswers.set(TransportArrangerPage, TransportArranger.values.head)
    )) {
      val result = controller.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill(TransportArranger.values.head), NormalMode)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Test() {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", TransportArranger.values.head.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test() {

      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", TransportArranger.values.head.toString)))

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
