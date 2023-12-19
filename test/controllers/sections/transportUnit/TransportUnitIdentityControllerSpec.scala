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

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.sections.transportUnit.TransportUnitIdentityFormProvider
import mocks.services.MockUserAnswersService
import models.sections.transportUnit.TransportUnitType
import models.sections.transportUnit.TransportUnitType.Tractor
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeTransportUnitNavigator
import pages.sections.transportUnit.{TransportUnitIdentityPage, TransportUnitTypePage}
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.transportUnit.TransportUnitIdentityView

import scala.concurrent.Future

class TransportUnitIdentityControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: TransportUnitIdentityFormProvider = new TransportUnitIdentityFormProvider()

  lazy val form: Form[String] = formProvider(TransportUnitType.FixedTransport)

  lazy val view: TransportUnitIdentityView = app.injector.instanceOf[TransportUnitIdentityView]

  class Test(userAnswers: Option[UserAnswers]) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new TransportUnitIdentityController(
      messagesApi,
      mockUserAnswersService,
      new FakeTransportUnitNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "TransportUnitIdentity Controller" - {

    "must return OK and the correct view for a GET if Transport unit type is answered" in new Test(Some(
      emptyUserAnswers.set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form, TransportUnitType.FixedTransport, testIndex1, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered and transport unit type is answered" in new Test(Some(
      emptyUserAnswers
        .set(TransportUnitIdentityPage(testIndex1), "answer")
        .set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill("answer"), TransportUnitType.FixedTransport, testIndex1, NormalMode)(dataRequest(request, emptyUserAnswers
          .set(TransportUnitIdentityPage(testIndex1), "answer")
          .set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport)), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Test(Some(
      emptyUserAnswers
        .set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport)
    )) {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to index route when no TrasnportUnitType has been answered" in new Test(Some(
      emptyUserAnswers.set(TransportUnitIdentityPage(testIndex1), "answer")
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(
      emptyUserAnswers
        .set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport)
    )) {
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual
        view(boundForm, TransportUnitType.FixedTransport, testIndex1, NormalMode)(dataRequest(request, emptyUserAnswers
          .set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport)), messages(request)).toString
    }

    "must redirect to transport unit index controller for a GET if the index in the url is not valid" in new Test(Some(
      emptyUserAnswers.set(TransportUnitIdentityPage(testIndex1), "answer").set(TransportUnitTypePage(testIndex1), Tractor)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to transport unit index controller for a POST if the index in the url is not valid" in new Test(Some(
      emptyUserAnswers.set(TransportUnitIdentityPage(testIndex1), "answer2").set(TransportUnitTypePage(testIndex1), Tractor)
    )) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex2, NormalMode)(request.withFormUrlEncodedBody("value" -> "true"))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
