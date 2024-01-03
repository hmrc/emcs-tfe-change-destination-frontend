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
import fixtures.TransportUnitFixtures
import forms.sections.transportUnit.TransportSealTypeFormProvider
import mocks.services.MockUserAnswersService
import models.sections.transportUnit.TransportUnitType.{Container, Tractor}
import models.sections.transportUnit.{TransportSealTypeModel, TransportUnitType}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeTransportUnitNavigator
import pages.sections.transportUnit._
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.transportUnit.TransportSealTypeView

import scala.concurrent.Future

class TransportSealTypeControllerSpec extends SpecBase with MockUserAnswersService with TransportUnitFixtures {

  lazy val formProvider: TransportSealTypeFormProvider = new TransportSealTypeFormProvider()
  lazy val form: Form[TransportSealTypeModel] = formProvider()
  lazy val view: TransportSealTypeView = app.injector.instanceOf[TransportSealTypeView]

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new TransportSealTypeController(
      messagesApi,
      mockUserAnswersService,
      fakeUserAllowListAction,
      new FakeTransportUnitNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse),
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }


  "TransportSealType Controller" - {

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(emptyUserAnswers
      .set(TransportUnitTypePage(testIndex1), Container)
      .set(TransportSealTypePage(testIndex1), transportSealTypeModelMax)
    )) {
      val result = controller.onPageLoad(testErn, testArc, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = form.fill(transportSealTypeModelMax),
        transportUnitType = TransportUnitType.Container,
        onSubmitCall = controllers.sections.transportUnit.routes.TransportSealTypeController.onSubmit(testErn, testArc, testIndex1, NormalMode)
      )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must populate the view correctly on a GET when only the seal type has previously been answered" in new Fixture(Some(emptyUserAnswers
      .set(TransportUnitTypePage(testIndex1), Container)
      .set(TransportSealTypePage(testIndex1), transportSealTypeModelMin)
    )) {
      val result = controller.onPageLoad(testErn, testArc, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = form.fill(transportSealTypeModelMin),
        transportUnitType = TransportUnitType.Container,
        onSubmitCall = controllers.sections.transportUnit.routes.TransportSealTypeController.onSubmit(testErn, testArc, testIndex1, NormalMode)
      )(dataRequest(request, userAnswers.get), messages(request)).toString
    }


    "must redirect to journey recovery when no answer has been provided for transport unit type onPageLoad" in new Fixture(Some(
      emptyUserAnswers.set(TransportUnitIdentityPage(testIndex1), "answer")
    )) {

      val result = controller.onPageLoad(testErn, testArc, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to the next page when valid data is submitted" in new Fixture(Some(
      emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Container)
    )) {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val result = controller.onSubmit(testErn, testArc, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(
      emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Container)
    )) {
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testArc, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(
        form = boundForm,
        transportUnitType = TransportUnitType.Container,
        onSubmitCall = controllers.sections.transportUnit.routes.TransportSealTypeController.onSubmit(testErn, testArc, testIndex1, NormalMode)
      )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to journey recovery when no answer has been provided for transport unit type onSubmit" in new Fixture(Some(
      emptyUserAnswers.set(TransportUnitIdentityPage(testIndex1), "answer")
    )) {

      val result = controller.onSubmit(testErn, testArc, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

      val result = controller.onPageLoad(testErn, testArc, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val result = controller.onSubmit(testErn, testArc, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to transport unit index controller for a GET if the index in the url is not valid" in new Fixture(Some(
      emptyUserAnswers
        .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("some", None))
        .set(TransportUnitTypePage(testIndex1), Tractor)
    )) {
      val result = controller.onPageLoad(testErn, testArc, testIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testArc).url
    }

    "must redirect to transport unit index controller for a POST if the index in the url is not valid" in new Fixture(Some(
      emptyUserAnswers
        .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("some", None))
        .set(TransportUnitTypePage(testIndex1), Tractor)
    )) {
      val result = controller.onSubmit(testErn, testArc, testIndex2, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testArc).url
    }
  }
}
