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
import forms.sections.transportUnit.TransportUnitTypeFormProvider
import mocks.services.MockUserAnswersService
import models.sections.transportUnit.TransportUnitType.{Container, FixedTransport, Tractor}
import models.sections.transportUnit.{TransportSealTypeModel, TransportUnitType}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeTransportUnitNavigator
import pages.sections.transportUnit._
import play.api.Play.materializer
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.transportUnit.TransportUnitTypeView

import scala.concurrent.Future

class TransportUnitTypeControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: TransportUnitTypeFormProvider = new TransportUnitTypeFormProvider()

  lazy val form: Form[TransportUnitType] = formProvider()

  lazy val view: TransportUnitTypeView = app.injector.instanceOf[TransportUnitTypeView]

  class Test(userAnswers: Option[UserAnswers]) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new TransportUnitTypeController(
      messagesApi,
      mockUserAnswersService,
      fakeBetaAllowListAction,
      new FakeTransportUnitNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse.copy(transportDetails = Seq.empty)),
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "TransportUnitType Controller" - {

    "must return OK and the correct view for a GET" in new Test(Some(emptyUserAnswers)) {

      val result = controller.onPageLoad(testErn, testArc, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, testIndex1, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      emptyUserAnswers.set(TransportUnitTypePage(testIndex1), TransportUnitType.values.head)
    )) {

      val result = controller.onPageLoad(testErn, testArc, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(TransportUnitType.values.head), testIndex1, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Test(Some(emptyUserAnswers)) {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val result =
        controller.onSubmit(testErn, testArc, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", TransportUnitType.values.head.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to the next page when valid data is submitted " +
      "(clearing down any subsequent pages when Fixed Transport Installations is selected)" in new Test(Some(
      emptyUserAnswers
        .set(TransportUnitTypePage(testIndex1), Container)
        .set(TransportUnitIdentityPage(testIndex1), "identity1")
        .set(TransportSealChoicePage(testIndex1), true)
        .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)
        .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("seal1", Some("moreinfo")))
        .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("more information about tu"))
    )) {

      val expectedAnswers: UserAnswers = emptyUserAnswers.set(TransportUnitTypePage(testIndex1), FixedTransport)
      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val result =
        controller.onSubmit(testErn, testArc, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", TransportUnitType.FixedTransport.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to the next page when valid data is submitted " +
      "(not clearing down any subsequent pages when Fixed Transport Installations is not selected)" in new Test(Some(
      emptyUserAnswers
        .set(TransportUnitTypePage(testIndex1), Container)
        .set(TransportUnitIdentityPage(testIndex1), "identity1")
        .set(TransportSealChoicePage(testIndex1), true)
        .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)
        .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("seal1", Some("moreinfo")))
        .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("more information about tu"))
    )) {

      val expectedAnswers: UserAnswers = emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Tractor)
        .set(TransportUnitIdentityPage(testIndex1), "identity1")
        .set(TransportSealChoicePage(testIndex1), true)
        .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)
        .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("seal1", Some("moreinfo")))
        .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("more information about tu"))
      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val result =
        controller.onSubmit(testErn, testArc, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", TransportUnitType.Tractor.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to the index controller if index is not next in index list for GET" in new Test(Some(emptyUserAnswers)) {

      val result = controller.onPageLoad(testErn, testArc, testIndex3, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testArc).url
    }

    "must redirect to the index controller if index is not next in index list for POST" in new Test(Some(emptyUserAnswers)) {

      val result =
        controller.onSubmit(testErn, testArc, testIndex3, NormalMode)(request.withFormUrlEncodedBody(("value", TransportUnitType.values.head.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testArc).url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(emptyUserAnswers)) {

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller.onSubmit(testErn, testArc, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "invalid value")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, testIndex1, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {

      val result = controller.onPageLoad(testErn, testArc, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {

      val result = controller.onSubmit(testErn, testArc, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
