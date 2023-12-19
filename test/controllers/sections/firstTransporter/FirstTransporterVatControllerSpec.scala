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

package controllers.sections.firstTransporter

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.sections.firstTransporter.FirstTransporterVatFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeFirstTransporterNavigator
import pages.sections.firstTransporter.FirstTransporterVatPage
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.firstTransporter.FirstTransporterVatView

import scala.concurrent.Future

class FirstTransporterVatControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: FirstTransporterVatFormProvider = new FirstTransporterVatFormProvider()
  lazy val form: Form[String] = formProvider()
  lazy val view: FirstTransporterVatView = app.injector.instanceOf[FirstTransporterVatView]

  lazy val firstTransporterVatRoute: String =
    controllers.sections.firstTransporter.routes.FirstTransporterVatController.onPageLoad(testErn, testDraftId, NormalMode).url
  lazy val firstTransporterVatSubmitAction: Call =
    controllers.sections.firstTransporter.routes.FirstTransporterVatController.onSubmit(testErn, testDraftId, NormalMode)
  lazy val firstTransporterVatNonGBVATRoute: String =
    controllers.sections.firstTransporter.routes.FirstTransporterVatController.onNonGbVAT(testErn, testDraftId).url

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val request = FakeRequest(GET, firstTransporterVatRoute)

    lazy val testController = new FirstTransporterVatController(
      messagesApi,
      mockUserAnswersService,
      new FakeFirstTransporterNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      formProvider,
      messagesControllerComponents,
      view
    )

  }

  "FirstTransporterVat Controller" - {
    "must return OK and the correct view for a GET" in new Fixture() {
      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, firstTransporterVatSubmitAction)(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(emptyUserAnswers.set(FirstTransporterVatPage, "answer"))) {

      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill("answer"), firstTransporterVatSubmitAction)(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val req = FakeRequest(POST, firstTransporterVatRoute).withFormUrlEncodedBody(("value", "answer"))

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to the next page when the NONGBVAT link" in new Fixture() {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val req = FakeRequest(GET, firstTransporterVatNonGBVATRoute)

      val result = testController.onNonGbVAT(testErn, testDraftId)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      val req = FakeRequest(POST, firstTransporterVatRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, firstTransporterVatSubmitAction)(dataRequest(request), messages(request)).toString
    }


    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val req = FakeRequest(POST, firstTransporterVatRoute).withFormUrlEncodedBody(("value", "answer"))

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
