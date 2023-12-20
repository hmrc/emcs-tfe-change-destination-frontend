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
import forms.sections.documents.DocumentsRemoveFromListFormProvider
import mocks.services.MockUserAnswersService
import models.sections.documents.DocumentsAddToList
import models.{Index, UserAnswers}
import navigation.FakeNavigators.FakeDocumentsNavigator
import pages.sections.documents.{DocumentReferencePage, DocumentsAddToListPage, ReferenceAvailablePage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.documents.DocumentsRemoveFromListView

import scala.concurrent.Future

class DocumentsRemoveFromListControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: DocumentsRemoveFromListFormProvider = new DocumentsRemoveFromListFormProvider()

  def documentsRemoveUnitRoute(idx: Index = 0): String =
    controllers.sections.documents.routes.DocumentsRemoveFromListController.onPageLoad(testErn, testDraftId, idx).url

  lazy val view: DocumentsRemoveFromListView = app.injector.instanceOf[DocumentsRemoveFromListView]

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers
    .set(ReferenceAvailablePage(0), true)
    .set(DocumentReferencePage(0), "reference")
  )) {

    val request = FakeRequest(GET, documentsRemoveUnitRoute())

    lazy val testController = new DocumentsRemoveFromListController(
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

  "DocumentsRemoveFromList Controller" - {
    "must return OK and the correct view for a GET" in new Fixture() {
      val result = testController.onPageLoad(testErn, testDraftId, 0)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(formProvider(0), 0)(dataRequest(request), messages(request)).toString
    }

    "must redirect to the index controller when index is out of bounds (for GET)" in new Fixture() {
      val req = FakeRequest(GET, documentsRemoveUnitRoute(1))
      val result = testController.onPageLoad(testErn, testDraftId, 1)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Add to List when the user answers POSTs an answer of no" in new Fixture() {
      val req = FakeRequest(POST, documentsRemoveUnitRoute()).withFormUrlEncodedBody(("value", "false"))
      val result = testController.onSubmit(testErn, testDraftId, 0)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual
        routes.DocumentsAddToListController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to the index controller when the user answers yes (removing the document and DocumentsAddToListPage)" in new Fixture(
      Some(emptyUserAnswers
        .set(ReferenceAvailablePage(0), true)
        .set(DocumentReferencePage(0), "reference")
        .set(ReferenceAvailablePage(1), false)
        .set(DocumentReferencePage(1), "description")
        .set(DocumentsAddToListPage, DocumentsAddToList.No)
      )
    ) {
      val answersAfterRemoval = emptyUserAnswers
        .set(ReferenceAvailablePage(0), false)
        .set(DocumentReferencePage(0), "description")

      MockUserAnswersService.set(answersAfterRemoval).returns(Future.successful(answersAfterRemoval))

      val req = FakeRequest(POST, documentsRemoveUnitRoute()).withFormUrlEncodedBody(("value", "true"))
      val result = testController.onSubmit(testErn, testDraftId, 0)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to the index controller when index is out of bounds (for POST)" in new Fixture() {
      val req = FakeRequest(POST, routes.DocumentsRemoveFromListController.onPageLoad(testErn, testDraftId, 1).url)
        .withFormUrlEncodedBody(("value", "true"))

      val result = testController.onSubmit(testErn, testDraftId, 1)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      val req = FakeRequest(POST, documentsRemoveUnitRoute()).withFormUrlEncodedBody(("value", ""))
      val boundForm = formProvider(0).bind(Map("value" -> ""))
      val result = testController.onSubmit(testErn, testDraftId, 0)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, 0)(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = testController.onPageLoad(testErn, testDraftId, 0)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val req = FakeRequest(POST, documentsRemoveUnitRoute()).withFormUrlEncodedBody(("value", "true"))
      val result = testController.onSubmit(testErn, testDraftId, 0)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
