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
import controllers.actions.FakeMovementAction
import forms.sections.transportUnit.TransportUnitGiveMoreInformationChoiceFormProvider
import mocks.services.MockUserAnswersService
import models.sections.transportUnit.TransportUnitType.{Container, Tractor}
import models.{Index, NormalMode, UserAnswers}
import navigation.TransportUnitNavigator
import pages.sections.transportUnit.{TransportUnitGiveMoreInformationChoicePage, TransportUnitGiveMoreInformationPage, TransportUnitIdentityPage, TransportUnitTypePage}
import play.api.data.Form
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.transportUnit.TransportUnitGiveMoreInformationChoiceView

import scala.concurrent.Future

class TransportUnitGiveMoreInformationChoiceControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  implicit lazy val messages: Messages = messagesApi.preferred(request)

  lazy val formProvider: TransportUnitGiveMoreInformationChoiceFormProvider = new TransportUnitGiveMoreInformationChoiceFormProvider()

  lazy val form: Form[Boolean] = formProvider(Tractor)

  lazy val view: TransportUnitGiveMoreInformationChoiceView = app.injector.instanceOf[TransportUnitGiveMoreInformationChoiceView]

  class Test(val userAnswers: Option[UserAnswers]) {
    lazy val controller = new TransportUnitGiveMoreInformationChoiceController(
      messagesApi,
      mockUserAnswersService,
      fakeUserAllowListAction,
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

  "TransportUnitGiveMoreInformationChoice Controller" - {

    "must return OK and the correct view for a GET" in new Test(Some(
      emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Tractor)
    )) {
      val result = controller.onPageLoad(testErn, testArc, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, testIndex1, NormalMode, Tractor)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      emptyUserAnswers
        .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)
        .set(TransportUnitTypePage(testIndex1), Tractor)
    )) {
      val result = controller.onPageLoad(testErn, testArc, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(true), testIndex1, NormalMode, Tractor)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to transport unit index controller for a GET if the index in the url is not valid" in new Test(Some(
      emptyUserAnswers
        .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)
        .set(TransportUnitTypePage(testIndex1), Tractor)
    )) {
      val result = controller.onPageLoad(testErn, testArc, testIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testArc).url
    }

    "must redirect to transport unit index controller for a POST if the index in the url is not valid" in new Test(Some(
      emptyUserAnswers
        .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)
        .set(TransportUnitTypePage(testIndex1), Tractor)
    )) {
      val result = controller.onSubmit(testErn, testArc, testIndex2, NormalMode)(request.withFormUrlEncodedBody("value" -> "true"))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testArc).url
    }

    "must redirect to journey recovery for a GET if there is not a transport unit type found in the users answers" in new Test(Some(
      emptyUserAnswers.set(TransportUnitIdentityPage(testIndex1), "answer")
    )) {
      val result = controller.onPageLoad(testErn, testArc, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to the next page (TU06) when valid data is submitted (answer is Yes)" in new Test(Some(
      emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Tractor)
    )) {

      MockUserAnswersService
        .set()
        .returns(
          Future.successful(
            emptyUserAnswers
              .set(TransportUnitTypePage(testIndex1), Tractor)
              .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)))

      val result = controller.onSubmit(testErn, testArc, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.TransportUnitGiveMoreInformationController.onPageLoad(testErn, testArc, testIndex1, NormalMode).url
    }

    "must redirect to the next page (TU07) when valid data is submitted (answer is No)" in new Test(Some(
      emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Tractor)
    )) {

      MockUserAnswersService
        .set()
        .returns(
          Future.successful(
            emptyUserAnswers
              .set(TransportUnitTypePage(testIndex1), Tractor)
              .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)))

      val result = controller.onSubmit(testErn, testArc, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "false")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.TransportUnitsAddToListController.onPageLoad(testErn, testArc).url
    }

    "must cleanse the give more information page (TU06) when answering no" in new Test(Some(
      emptyUserAnswers
        .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)
        .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("beans"))
        .set(TransportUnitTypePage(testIndex1), Container)
    )) {
      val expectedAnswers = emptyUserAnswers
        .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), false)
        .set(TransportUnitTypePage(testIndex1), Container)

      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val result = controller.onSubmit(testErn, testArc, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "false")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.TransportUnitsAddToListController.onPageLoad(testErn, testArc).url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(
      emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Tractor)
    )) {
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testArc, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, testIndex1, NormalMode, Tractor)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to journey recovery controller for a POST if there is not a transport unit type found" in new Test(Some(
      emptyUserAnswers.set(TransportUnitIdentityPage(Index(0)), "answer")
    )) {
      val result = controller.onSubmit(testErn, testArc, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testArc, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testArc, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
