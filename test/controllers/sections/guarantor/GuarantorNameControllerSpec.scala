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
import forms.sections.guarantor.GuarantorNameFormProvider
import mocks.services.MockUserAnswersService
import models.response.emcsTfe.{GuarantorType, MovementGuaranteeModel}
import models.sections.guarantor.GuarantorArranger.{Consignee, GoodsOwner}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeGuarantorNavigator
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorNamePage}
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.guarantor.GuarantorNameView

import scala.concurrent.Future

class GuarantorNameControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: GuarantorNameFormProvider = new GuarantorNameFormProvider()
  lazy val form: Form[String] = formProvider()
  lazy val view: GuarantorNameView = app.injector.instanceOf[GuarantorNameView]

  lazy val guarantorNameRoute: String = controllers.sections.guarantor.routes.GuarantorNameController.onPageLoad(testErn, testArc, NormalMode).url

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val request = FakeRequest(GET, guarantorNameRoute)

    lazy val testController = new GuarantorNameController(
      messagesApi,
      mockUserAnswersService,
      new FakeGuarantorNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(GuarantorType.NoGuarantor, None))),
      fakeUserAllowListAction,
      formProvider,
      messagesControllerComponents,
      view
    )
  }

  "GuarantorArrangerName Controller" - {
    "must return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers.set(GuarantorArrangerPage, GoodsOwner))) {
      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, GoodsOwner, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(emptyUserAnswers
        .set(GuarantorArrangerPage, GoodsOwner)
        .set(GuarantorNamePage, "answer"))) {

      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill("answer"), GoodsOwner, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture(Some(emptyUserAnswers.set(GuarantorArrangerPage, GoodsOwner))) {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers.set(GuarantorArrangerPage, GoodsOwner)))

      val req = FakeRequest(POST, guarantorNameRoute).withFormUrlEncodedBody(("value", "answer"))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers.set(GuarantorArrangerPage, GoodsOwner))) {
      val req = FakeRequest(POST, guarantorNameRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, GoodsOwner, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to the guarantor index controller for a GET if no guarantor arranger value is found" in new Fixture() {
      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual
        controllers.sections.guarantor.routes.GuarantorIndexController.onPageLoad(testErn, testArc).url
    }

    "must redirect to CYA for a GET if the guarantor arranger value is invalid for this controller/page" in new Fixture(
      Some(emptyUserAnswers.set(GuarantorArrangerPage, Consignee))) {

      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.guarantor.routes.GuarantorCheckAnswersController.onPageLoad(testErn, testArc).url
    }


    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val req = FakeRequest(POST, guarantorNameRoute).withFormUrlEncodedBody(("value", "answer"))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
