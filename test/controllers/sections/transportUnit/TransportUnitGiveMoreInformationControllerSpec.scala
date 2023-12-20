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
import forms.sections.transportUnit.TransportUnitGiveMoreInformationFormProvider
import mocks.services.MockUserAnswersService
import models.sections.transportUnit.TransportUnitType.Tractor
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeTransportUnitNavigator
import pages.sections.transportUnit.{TransportUnitGiveMoreInformationPage, TransportUnitIdentityPage, TransportUnitTypePage}
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.transportUnit.TransportUnitGiveMoreInformationView

import scala.concurrent.Future

class TransportUnitGiveMoreInformationControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: TransportUnitGiveMoreInformationFormProvider = new TransportUnitGiveMoreInformationFormProvider()

  lazy val form: Form[Option[String]] = formProvider()

  lazy val view: TransportUnitGiveMoreInformationView = app.injector.instanceOf[TransportUnitGiveMoreInformationView]

  class Test(val userAnswers: Option[UserAnswers]) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new TransportUnitGiveMoreInformationController(
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

  "TransportUnitGiveMoreInformation Controller" - {

    "must return OK and the correct view for a GET" in new Test(Some(
      emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Tractor)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, testIndex1, NormalMode, Tractor)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      emptyUserAnswers
        .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("answer"))
        .set(TransportUnitTypePage(testIndex1), Tractor)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill(Some("answer")), testIndex1, NormalMode, Tractor)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to journey recovery for a GET if there is not a transport unit type found in the users answers" in new Test(Some(
      emptyUserAnswers.set(TransportUnitIdentityPage(testIndex1), "answer")
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to the next page" - {
      "when valid data is submitted" in new Test(Some(
        emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Tractor)
      )) {
        val answer = Some("answer")

        MockUserAnswersService
          .set(emptyUserAnswers
            .set(TransportUnitTypePage(testIndex1), Tractor)
            .set(TransportUnitGiveMoreInformationPage(testIndex1), answer))
          .returns(
            Future.successful(
              emptyUserAnswers
                .set(TransportUnitTypePage(testIndex1), Tractor)
                .set(TransportUnitGiveMoreInformationPage(testIndex1), answer)))

        val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
      "when empty data is submitted" in new Test(Some(
        emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Tractor)
      )) {
        val answer = None

        MockUserAnswersService
          .set(emptyUserAnswers
            .set(TransportUnitTypePage(testIndex1), Tractor)
            .set(TransportUnitGiveMoreInformationPage(testIndex1), answer))
          .returns(
            Future.successful(
              emptyUserAnswers
                .set(TransportUnitTypePage(testIndex1), Tractor)
                .set(TransportUnitGiveMoreInformationPage(testIndex1), answer)))

        val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
      "when only whitespace data is submitted" in new Test(Some(
        emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Tractor)
      )) {
        val answer = None

        MockUserAnswersService
          .set(emptyUserAnswers
            .set(TransportUnitTypePage(testIndex1), Tractor)
            .set(TransportUnitGiveMoreInformationPage(testIndex1), answer))
          .returns(
            Future.successful(
              emptyUserAnswers
                .set(TransportUnitTypePage(testIndex1), Tractor)
                .set(TransportUnitGiveMoreInformationPage(testIndex1), answer)))

        val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value",
          """
            |
            |
            |
            |
            |
            |
            |""".stripMargin)))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(
      emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Tractor)
    )) {
      val boundForm = form.bind(Map("value" -> """<script>alert("hi")</script>"""))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", """<script>alert("hi")</script>""")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, testIndex1, NormalMode, Tractor)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to journey recovery for a POST if there is not a transport unit type found" in new Test(Some(
      emptyUserAnswers.set(TransportUnitIdentityPage(testIndex1), "answer")
    )) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to transport unit index controller for a GET if the index in the url is not valid" in new Test(Some(
      emptyUserAnswers
        .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("answer"))
        .set(TransportUnitTypePage(testIndex1), Tractor)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to transport unit index controller for a POST if the index in the url is not valid" in new Test(Some(
      emptyUserAnswers
        .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("answer2"))
        .set(TransportUnitTypePage(testIndex1), Tractor)
    )) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex2, NormalMode)(request.withFormUrlEncodedBody("value" -> "true"))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
