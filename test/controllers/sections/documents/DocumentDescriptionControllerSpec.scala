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

package controllers.sections.documents

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import fixtures.DocumentTypeFixtures
import forms.sections.documents.DocumentDescriptionFormProvider
import mocks.services.MockUserAnswersService
import models.{Index, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeDocumentsNavigator
import pages.sections.documents._
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.documents.DocumentDescriptionView

import scala.concurrent.Future

class DocumentDescriptionControllerSpec extends SpecBase with MockUserAnswersService with DocumentTypeFixtures {

  lazy val formProvider: DocumentDescriptionFormProvider = new DocumentDescriptionFormProvider()
  lazy val form: Form[String] = formProvider()
  lazy val view: DocumentDescriptionView = app.injector.instanceOf[DocumentDescriptionView]

  def onPageLoadRoute(idx: Index): String =
    routes.DocumentDescriptionController.onPageLoad(testErn, testDraftId, idx, NormalMode).url

  def onSubmitCall(idx: Index): Call =
    routes.DocumentDescriptionController.onSubmit(testErn, testDraftId, idx, NormalMode)

  class Setup(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val request = FakeRequest(GET, onPageLoadRoute(0))

    lazy val testController = new DocumentDescriptionController(
      messagesApi,
      mockUserAnswersService,
      fakeUserAllowListAction,
      new FakeDocumentsNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      formProvider,
      messagesControllerComponents,
      view
    )
  }

  "DocumentDescription Controller" - {
    "for GET onPageLoad" - {
      "must return OK and the correct view for a GET" in new Setup(
        Some(emptyUserAnswers.set(DocumentTypePage(0), documentTypeOtherModel))) {

        val result = testController.onPageLoad(testErn, testDraftId, 0, NormalMode)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form,
          onSubmitCall = onSubmitCall(0)
        )(dataRequest(request), messages(request)).toString
      }

      "must populate the view correctly on a GET when the question has previously been answered" in new Setup(
        Some(emptyUserAnswers
          .set(DocumentTypePage(0), documentTypeOtherModel)
          .set(DocumentDescriptionPage(0), "answer"))) {

        val result = testController.onPageLoad(testErn, testDraftId, 0, NormalMode)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form.fill("answer"),
          onSubmitCall = onSubmitCall(0)
        )(dataRequest(request), messages(request)).toString
      }

      "must redirect to DocumentsIndexController when there are no current documents in UserAnswers" in new Setup() {
        val req = FakeRequest(GET, onPageLoadRoute(1))

        val result = testController.onPageLoad(testErn, testDraftId, 0, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
      }

      "must redirect to DocumentsIndexController when the idx is greater than the next valid document idx" in new Setup(
        Some(emptyUserAnswers.set(DocumentTypePage(0), documentTypeOtherModel))) {

        val req = FakeRequest(GET, onPageLoadRoute(1))

        val result = testController.onPageLoad(testErn, testDraftId, 1, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
      }

      "must redirect to DocumentsIndexController when the idx is less than 0" in new Setup(
        Some(emptyUserAnswers.set(DocumentTypePage(0), documentTypeOtherModel))) {

        val req = FakeRequest(GET, onPageLoadRoute(-1))

        val result = testController.onPageLoad(testErn, testDraftId, -1, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in new Setup(None) {
        val result = testController.onPageLoad(testErn, testDraftId, 0, NormalMode)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "for POST onSubmit" - {
      "must redirect to the next page when valid data is submitted" in new Setup(
        Some(emptyUserAnswers.set(DocumentTypePage(0), documentTypeOtherModel))) {

        MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

        val req = FakeRequest(POST, onPageLoadRoute(0)).withFormUrlEncodedBody(("value", "answer"))

        val result = testController.onSubmit(testErn, testDraftId, 0, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Setup(
        Some(emptyUserAnswers.set(DocumentTypePage(0), documentTypeOtherModel))) {

        val req = FakeRequest(POST, onPageLoadRoute(0)).withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val result = testController.onSubmit(testErn, testDraftId, 0, NormalMode)(req)

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          form = boundForm,
          onSubmitCall = onSubmitCall(0)
        )(dataRequest(request), messages(request)).toString
      }

      "must redirect to DocumentsIndexController when there are no current documents in UserAnswers" in new Setup() {
        val req = FakeRequest(POST, onPageLoadRoute(0)).withFormUrlEncodedBody(("value", "reference"))

        val result = testController.onSubmit(testErn, testDraftId, 0, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
      }

      "must redirect to DocumentsIndexController when the idx is greater than the next valid document idx" in new Setup(
        Some(emptyUserAnswers.set(DocumentTypePage(0), documentTypeOtherModel))) {

        val req = FakeRequest(POST, onPageLoadRoute(1)).withFormUrlEncodedBody(("value", "reference"))

        val result = testController.onSubmit(testErn, testDraftId, 1, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
      }

      "must redirect to DocumentsIndexController when the idx is less than 0" in new Setup(
        Some(emptyUserAnswers.set(DocumentTypePage(0), documentTypeOtherModel))) {

        val req = FakeRequest(POST, onPageLoadRoute(-1)).withFormUrlEncodedBody(("value", "reference"))

        val result = testController.onSubmit(testErn, testDraftId, -1, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
      }

      "must redirect to Journey Recovery for a POST if no existing data is found" in new Setup(None) {
        val req = FakeRequest(POST, onPageLoadRoute(0)).withFormUrlEncodedBody(("value", "answer"))

        val result = testController.onSubmit(testErn, testDraftId, 0, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
