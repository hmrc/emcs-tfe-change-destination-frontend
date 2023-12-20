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

package controllers.sections.guarantor

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.sections.guarantor.GuarantorVatFormProvider
import mocks.services.MockUserAnswersService
import models.sections.guarantor.GuarantorArranger.Transporter
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeGuarantorNavigator
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorRequiredPage, GuarantorVatPage}
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.guarantor.GuarantorVatView

import scala.concurrent.Future

class GuarantorVatControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: GuarantorVatFormProvider = new GuarantorVatFormProvider()
  lazy val form: Form[String] = formProvider()
  lazy val view: GuarantorVatView = app.injector.instanceOf[GuarantorVatView]

  lazy val guarantorVatRoute: String = controllers.sections.guarantor.routes.GuarantorVatController.onPageLoad(testErn, testDraftId, NormalMode).url
  lazy val guarantorVatSubmitRoute: String = controllers.sections.guarantor.routes.GuarantorVatController.onSubmit(testErn, testDraftId, NormalMode).url

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val request = FakeRequest(GET, guarantorVatRoute)

    lazy val testController = new GuarantorVatController(
      messagesApi,
      mockUserAnswersService,
      new FakeGuarantorNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      formProvider,
      messagesControllerComponents,
      view
    )
  }

  "GuarantorVat Controller" - {
    "must return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers
      .set(GuarantorRequiredPage, true)
      .set(GuarantorArrangerPage, Transporter))) {

      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, Transporter, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(emptyUserAnswers
        .set(GuarantorRequiredPage, true)
        .set(GuarantorArrangerPage, Transporter)
        .set(GuarantorVatPage, "answer"))) {

      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill("answer"), Transporter, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture(
      Some(emptyUserAnswers
        .set(GuarantorRequiredPage, true)
        .set(GuarantorArrangerPage, Transporter))) {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers
        .set(GuarantorRequiredPage, true)
        .set(GuarantorArrangerPage, Transporter)))

      val req = FakeRequest(POST, guarantorVatRoute).withFormUrlEncodedBody(("value", "answer"))

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to the next page when the NONGBVAT link is clicked" in new Fixture(
      Some(emptyUserAnswers
        .set(GuarantorRequiredPage, true)
        .set(GuarantorArrangerPage, Transporter))) {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers
        .set(GuarantorRequiredPage, true)
        .set(GuarantorArrangerPage, Transporter)))

      val req = FakeRequest(GET, controllers.sections.guarantor.routes.GuarantorVatController.onNonGbVAT(testErn, testDraftId, NormalMode).url)

      val result = testController.onNonGbVAT(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to the guarantor index controller for a GET if no guarantor arranger value is found" in new Fixture(
      Some(emptyUserAnswers
        .set(GuarantorRequiredPage, true))) {

      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual
        controllers.sections.guarantor.routes.GuarantorIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to the guarantor index controller for a POST if no guarantor arranger value is found" in new Fixture(
      Some(emptyUserAnswers
        .set(GuarantorRequiredPage, true))) {

      val req = FakeRequest(POST, guarantorVatSubmitRoute)

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual
        controllers.sections.guarantor.routes.GuarantorIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers
      .set(GuarantorRequiredPage, true)
      .set(GuarantorArrangerPage, Transporter))) {

      val req = FakeRequest(POST, guarantorVatRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, Transporter, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val req = FakeRequest(POST, guarantorVatRoute).withFormUrlEncodedBody(("value", "answer"))

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
