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

package controllers.sections.sad

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.sections.sad.SadRemoveDocumentFormProvider
import mocks.services.MockUserAnswersService
import models.UserAnswers
import navigation.SadNavigator
import pages.sections.sad.ImportNumberPage
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.sad.SadRemoveDocumentView

import scala.concurrent.Future

class SadRemoveDocumentControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: SadRemoveDocumentFormProvider = new SadRemoveDocumentFormProvider()
  lazy val form: Form[Boolean] = formProvider()
  lazy val view: SadRemoveDocumentView = app.injector.instanceOf[SadRemoveDocumentView]

  class Test(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new SadRemoveDocumentController(
      messagesApi,
      mockUserAnswersService,
      fakeUserAllowListAction,
      app.injector.instanceOf[SadNavigator],
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "SadRemoveDocument Controller" - {

    "must return OK and the correct view for a GET" in new Test(Some(
      emptyUserAnswers.set(ImportNumberPage(testIndex1), "answer")
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, testIndex1)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the index controller when index is out of bounds (for GET)" in new Test(Some(
      emptyUserAnswers
        .set(ImportNumberPage(testIndex1), "answer")
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex2)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.sad.routes.SadIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to add to list when the user answers no" in new Test(Some(
      emptyUserAnswers.set(ImportNumberPage(testIndex1), "answer1")
    )) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1)(request.withFormUrlEncodedBody(("value", "false")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual
        controllers.sections.sad.routes.SadAddToListController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to the index controller when the user answers yes (removing the Sad Document)" in new Test(Some(
      emptyUserAnswers
        .set(ImportNumberPage(testIndex1), "answer1")
        .set(ImportNumberPage(testIndex2), "answer2")
    )) {

      MockUserAnswersService.set(
        emptyUserAnswers.set(ImportNumberPage(testIndex1), "answer2")
      ).returns(Future.successful(
        emptyUserAnswers.set(ImportNumberPage(testIndex1), "answer2")
      ))
      val result = controller.onSubmit(testErn, testDraftId, testIndex1)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.sad.routes.SadIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to the index controller when index is out of bounds (for POST)" in new Test(Some(
      emptyUserAnswers
        .set(ImportNumberPage(testIndex1), "answer")
    )) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex2)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.sad.routes.SadIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(
      emptyUserAnswers.set(ImportNumberPage(testIndex1), "answer")
    )) {
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, testIndex1)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
