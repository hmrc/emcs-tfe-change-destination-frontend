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
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import controllers.routes
import forms.sections.guarantor.GuarantorRequiredFormProvider
import mocks.services.MockUserAnswersService
import models.response.emcsTfe.GuarantorType.{Consignor, NoGuarantor}
import models.response.emcsTfe.{GuarantorType, MovementGuaranteeModel}
import models.sections.guarantor.GuarantorArranger.Transporter
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeGuarantorNavigator
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorNamePage, GuarantorRequiredPage}
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.guarantor.GuarantorRequiredView

import scala.concurrent.Future

class GuarantorRequiredControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: GuarantorRequiredFormProvider = new GuarantorRequiredFormProvider()
  lazy val form: Form[Boolean] = formProvider()
  lazy val view: GuarantorRequiredView = app.injector.instanceOf[GuarantorRequiredView]

  lazy val guarantorRequiredRoute: String = controllers.sections.guarantor.routes.GuarantorRequiredController.onPageLoad(testErn, testArc, NormalMode).url

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers), guarantor: GuarantorType = NoGuarantor) {
    val request = FakeRequest(GET, guarantorRequiredRoute)

    lazy val testController = new GuarantorRequiredController(
      messagesApi,
      mockUserAnswersService,
      fakeUserAllowListAction,
      new FakeGuarantorNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(guarantor, None))),
      formProvider,
      messagesControllerComponents,
      view
    )

  }

  "GuarantorRequired Controller" - {

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(emptyUserAnswers.set(GuarantorRequiredPage, true))) {

      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(true), NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val req = FakeRequest(POST, guarantorRequiredRoute).withFormUrlEncodedBody(("value", "false"))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      val req = FakeRequest(POST, guarantorRequiredRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode)(dataRequest(request), messages(request)).toString
    }


    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val req = FakeRequest(POST, guarantorRequiredRoute).withFormUrlEncodedBody(("value", "true"))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must cleanse the guarantor section when answering no" in new Fixture(
      Some(emptyUserAnswers
        .set(GuarantorRequiredPage, true)
        .set(GuarantorArrangerPage, Transporter)
        .set(GuarantorNamePage, "a name"))) {

      val expectedAnswers = emptyUserAnswers.set(GuarantorRequiredPage, false)
      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val req = FakeRequest(POST, guarantorRequiredRoute).withFormUrlEncodedBody(("value", "false"))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
    }

    "must cleanse the guarantor section when answering yes" in new Fixture(
      Some(emptyUserAnswers
        .set(GuarantorRequiredPage, false)
        .set(GuarantorArrangerPage, Transporter)
        .set(GuarantorNamePage, "a name")), Consignor) {

      val expectedAnswers = emptyUserAnswers.set(GuarantorRequiredPage, true)
      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val req = FakeRequest(POST, guarantorRequiredRoute).withFormUrlEncodedBody(("value", "true"))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
    }
  }
}
