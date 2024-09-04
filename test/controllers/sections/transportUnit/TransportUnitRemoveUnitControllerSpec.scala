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
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import forms.sections.transportUnit.TransportUnitRemoveUnitFormProvider
import mocks.services.MockUserAnswersService
import models.UserAnswers
import models.sections.transportUnit.TransportUnitType
import navigation.TransportUnitNavigator
import pages.sections.transportUnit.TransportUnitTypePage
import play.api.Play.materializer
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.transportUnit.TransportUnitRemoveUnitView

import scala.concurrent.Future

class TransportUnitRemoveUnitControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: TransportUnitRemoveUnitFormProvider = new TransportUnitRemoveUnitFormProvider()

  lazy val form: Form[Boolean] = formProvider()

  lazy val view: TransportUnitRemoveUnitView = app.injector.instanceOf[TransportUnitRemoveUnitView]

  class Test(userAnswers: Option[UserAnswers]) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new TransportUnitRemoveUnitController(
      messagesApi,
      mockUserAnswersService,
      app.injector.instanceOf[TransportUnitNavigator],
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse),
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "TransportUnitRemoveUnit Controller" - {

    "must return OK and the correct view for a GET" in new Test(Some(
      emptyUserAnswers.set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport)
    )) {

      val result = controller.onPageLoad(testErn, testArc, testIndex1)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, testIndex1)(dataRequest(request), messages(request)).toString
    }

    "must redirect to the index controller when index is out of bounds (for GET)" in new Test(Some(
      emptyUserAnswers
        .set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport)
    )) {
      val result = controller.onPageLoad(testErn, testArc, testIndex2)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testArc).url
    }

    "must redirect to TU07 when the user answers no" in new Test(Some(
      emptyUserAnswers.set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport)
    )) {
      val result = controller.onSubmit(testErn, testArc, testIndex1)(request.withFormUrlEncodedBody(("value", "false")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual
        controllers.sections.transportUnit.routes.TransportUnitsAddToListController.onPageLoad(testErn, testArc).url
    }

    "must redirect to the index controller when the user answers yes (removing the transport unit)" in new Test(Some(
      emptyUserAnswers
        .set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport)
        .set(TransportUnitTypePage(testIndex2), TransportUnitType.Container)
    )) {

      MockUserAnswersService.set(
        emptyUserAnswers.set(TransportUnitTypePage(testIndex1), TransportUnitType.Container)
      ).returns(Future.successful(
        emptyUserAnswers.set(TransportUnitTypePage(testIndex1), TransportUnitType.Container)
      ))

      val result = controller.onSubmit(testErn, testArc, testIndex1)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testArc).url
    }

    "must redirect to the index controller when index is out of bounds (for POST)" in new Test(Some(
      emptyUserAnswers
        .set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport)
    )) {
      val result = controller.onPageLoad(testErn, testArc, testIndex2)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testArc).url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(
      emptyUserAnswers.set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport)
    )) {

      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testArc, testIndex1)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, testIndex1)(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {

      val result = controller.onPageLoad(testErn, testArc, testIndex1)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testArc, testIndex1)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
