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
import controllers.routes
import forms.sections.transportUnit.TransportSealChoiceFormProvider
import mocks.services.MockUserAnswersService
import models.sections.transportUnit.TransportSealTypeModel
import models.sections.transportUnit.TransportUnitType.{Container, Tractor}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeTransportUnitNavigator
import pages.sections.transportUnit._
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.transportUnit.TransportSealChoiceView

import scala.concurrent.Future

class TransportSealChoiceControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: TransportSealChoiceFormProvider = new TransportSealChoiceFormProvider()
  lazy val form: Form[Boolean] = formProvider(Container)(messages(FakeRequest()))
  lazy val view: TransportSealChoiceView = app.injector.instanceOf[TransportSealChoiceView]

  lazy val transportSealChoiceOnSubmit: Call =
    controllers.sections.transportUnit.routes.TransportSealChoiceController.onSubmit(testErn, testDraftId, testIndex1, NormalMode)

  class Setup(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new TransportSealChoiceController(
      messagesApi,
      mockUserAnswersService,
      fakeUserAllowListAction,
      new FakeTransportUnitNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )

  }

  "TransportSealChoice Controller" - {

    "must return OK and the correct view for a GET" in new Setup(Some(emptyUserAnswers
      .set(TransportUnitTypePage(testIndex1), Container)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = form,
        mode = NormalMode,
        transportUnitType = Container,
        onSubmitCall = transportSealChoiceOnSubmit
      )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Setup(Some(emptyUserAnswers
      .set(TransportUnitTypePage(testIndex1), Container)
      .set(TransportSealChoicePage(testIndex1), true)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = form.fill(true),
        mode = NormalMode,
        transportUnitType = Container,
        onSubmitCall = transportSealChoiceOnSubmit
      )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the transport unit index controller when missing the transport unit type answer" in new Setup(
      Some(emptyUserAnswers.set(TransportUnitIdentityPage(testIndex1), "answer"))) {

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual
        controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to the next page when valid data is submitted" in new Setup(Some(emptyUserAnswers
      .set(TransportUnitTypePage(testIndex1), Container)
    )) {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must cleanse the transport seal type (TU04) when answering no" in new Setup(Some(
      emptyUserAnswers
        .set(TransportSealChoicePage(testIndex1), true)
        .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("SEAL1", Some("xyz")))
        .set(TransportUnitTypePage(testIndex1), Container)
    )) {

      val expectedAnswers = emptyUserAnswers
        .set(TransportSealChoicePage(testIndex1), false)
        .set(TransportUnitTypePage(testIndex1), Container)

      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "false")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in new Setup(Some(emptyUserAnswers
      .set(TransportUnitTypePage(testIndex1), Container)
    )) {

      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(
        form = boundForm,
        mode = NormalMode,
        transportUnitType = Container,
        onSubmitCall = transportSealChoiceOnSubmit
      )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the transport unit index controller when transport unit type has not been answered" in new Setup(
      Some(emptyUserAnswers.set(TransportUnitIdentityPage(testIndex1), "answer"))) {

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual
        controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Setup(None) {

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Setup(None) {

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to transport unit index controller for a GET if the index in the url is not valid" in new Setup(Some(emptyUserAnswers
      .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("some", None))
      .set(TransportUnitTypePage(testIndex1), Tractor))) {

      val result = controller.onPageLoad(testErn, testDraftId, testIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to transport unit index controller for a POST if the index in the url is not valid" in new Setup(Some(emptyUserAnswers
      .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("some", None))
      .set(TransportUnitTypePage(testIndex1), Tractor))) {

      val result = controller.onSubmit(testErn, testDraftId, testIndex2, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testDraftId).url
    }
  }
}
