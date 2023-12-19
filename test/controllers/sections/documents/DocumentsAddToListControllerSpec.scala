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
import forms.sections.documents.DocumentsAddToListFormProvider
import mocks.services.MockUserAnswersService
import mocks.viewmodels.MockDocumentsAddToListHelper
import models.UserAnswers
import models.sections.documents.DocumentsAddToList
import navigation.FakeNavigators.FakeDocumentsNavigator
import pages.sections.documents._
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.documents.DocumentsAddToListView

import scala.concurrent.Future

class DocumentsAddToListControllerSpec extends SpecBase with MockUserAnswersService with MockDocumentsAddToListHelper with DocumentTypeFixtures {

  lazy val formProvider: DocumentsAddToListFormProvider = new DocumentsAddToListFormProvider()
  lazy val form: Form[DocumentsAddToList] = formProvider()
  lazy val view: DocumentsAddToListView = app.injector.instanceOf[DocumentsAddToListView]

  lazy val controllerRoute: String = routes.DocumentsAddToListController.onPageLoad(testErn, testDraftId).url
  lazy val onSubmitCall: Call = routes.DocumentsAddToListController.onSubmit(testErn, testDraftId)

  class Setup(val startingUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    val request = FakeRequest(GET, controllerRoute)

    lazy val testController = new DocumentsAddToListController(
      messagesApi,
      mockUserAnswersService,
      fakeUserAllowListAction,
      new FakeDocumentsNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(startingUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      formProvider,
      messagesControllerComponents,
      view,
      mockDocumentsAddToListHelper
    )

  }


  "DocumentsAddToList Controller" - {
    "GET onPageLoad" - {
      "must return OK and the correct view when there are NO InProgress items" in new Setup(
        Some(emptyUserAnswers
          .set(DocumentTypePage(0), documentTypeModel)
          .set(DocumentReferencePage(0), "reference"))) {

        MockDocumentsAddToListHelper.allDocumentsSummary()

        val result = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          formOpt = Some(form),
          onSubmitCall = onSubmitCall,
          documents = Seq.empty,
          showNoOption = true
        )(dataRequest(request), messages(request)).toString
      }

      "must return OK and the correct view when there ARE InProgress items" in new Setup(
        Some(emptyUserAnswers.set(DocumentTypePage(0), documentTypeModel))) {

        MockDocumentsAddToListHelper.allDocumentsSummary()

        val result = testController.onPageLoad(testErn, testDraftId)(request)


        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          formOpt = Some(form),
          onSubmitCall = onSubmitCall,
          documents = Seq.empty,
          showNoOption = false
        )(dataRequest(request), messages(request)).toString
      }

      "must return OK and the correct view when there MAX documents already added" in new Setup(Some(emptyUserAnswers
        .set(DocumentTypePage(0), documentTypeModel).set(DocumentReferencePage(0), "reference")
        .set(DocumentTypePage(1), documentTypeModel).set(DocumentReferencePage(1), "reference")
        .set(DocumentTypePage(2), documentTypeModel).set(DocumentReferencePage(2), "reference")
        .set(DocumentTypePage(3), documentTypeModel).set(DocumentReferencePage(3), "reference")
        .set(DocumentTypePage(4), documentTypeModel).set(DocumentReferencePage(4), "reference")
        .set(DocumentTypePage(5), documentTypeModel).set(DocumentReferencePage(5), "reference")
        .set(DocumentTypePage(6), documentTypeModel).set(DocumentReferencePage(6), "reference")
        .set(DocumentTypePage(7), documentTypeModel).set(DocumentReferencePage(7), "reference")
        .set(DocumentTypePage(8), documentTypeModel).set(DocumentReferencePage(8), "reference")
      )) {

        MockDocumentsAddToListHelper.allDocumentsSummary()

        val result = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          formOpt = None,
          onSubmitCall = onSubmitCall,
          documents = Seq.empty,
          showNoOption = false
        )(dataRequest(request), messages(request)).toString
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in new Setup(None) {
        val result = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "POST onSubmit" - {
      "must redirect to the next page when Yes is submitted" in new Setup(
        Some(emptyUserAnswers
          .set(ReferenceAvailablePage(0), true)
          .set(DocumentReferencePage(0), "reference"))) {

        MockUserAnswersService.set(startingUserAnswers.value).returns(Future.successful(startingUserAnswers.value))

        val req = FakeRequest(POST, controllerRoute).withFormUrlEncodedBody(("value", DocumentsAddToList.Yes.toString))

        val result = testController.onSubmit(testErn, testDraftId)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must redirect to the next page and wipe data when Yes is submitted with this page already answered" in new Setup(Some(
        emptyUserAnswers
          .set(ReferenceAvailablePage(0), true)
          .set(DocumentReferencePage(0), "reference")
          .set(DocumentsAddToListPage, DocumentsAddToList.No)
      )) {

        val updatedUserAnswers = emptyUserAnswers
          .set(ReferenceAvailablePage(0), true)
          .set(DocumentReferencePage(0), "reference")

        MockUserAnswersService.set(updatedUserAnswers).returns(Future.successful(updatedUserAnswers))

        val req = FakeRequest(POST, controllerRoute).withFormUrlEncodedBody(("value", DocumentsAddToList.Yes.toString))

        val result = testController.onSubmit(testErn, testDraftId)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must redirect to the next page when No is submitted" in new Setup(
        Some(emptyUserAnswers
          .set(ReferenceAvailablePage(0), true)
          .set(DocumentReferencePage(0), "reference"))) {

        val updatedAnswers = startingUserAnswers.value.set(DocumentsAddToListPage, DocumentsAddToList.No)

        MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

        val req = FakeRequest(POST, controllerRoute).withFormUrlEncodedBody(("value", DocumentsAddToList.No.toString))

        val result = testController.onSubmit(testErn, testDraftId)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must redirect to the next page when MoreLater is submitted" in new Setup(
        Some(emptyUserAnswers
          .set(ReferenceAvailablePage(0), true)
          .set(DocumentReferencePage(0), "reference"))) {

        val updatedAnswers = startingUserAnswers.value.set(DocumentsAddToListPage, DocumentsAddToList.MoreLater)

        MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

        val req = FakeRequest(POST, controllerRoute).withFormUrlEncodedBody(("value", DocumentsAddToList.MoreLater.toString))

        val result = testController.onSubmit(testErn, testDraftId)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must redirect to the next page when submitted with MAX documents already added" in new Setup(Some(emptyUserAnswers
        .set(DocumentTypePage(0), documentTypeModel).set(DocumentReferencePage(0), "reference")
        .set(DocumentTypePage(1), documentTypeModel).set(DocumentReferencePage(1), "reference")
        .set(DocumentTypePage(2), documentTypeModel).set(DocumentReferencePage(2), "reference")
        .set(DocumentTypePage(3), documentTypeModel).set(DocumentReferencePage(3), "reference")
        .set(DocumentTypePage(4), documentTypeModel).set(DocumentReferencePage(4), "reference")
        .set(DocumentTypePage(5), documentTypeModel).set(DocumentReferencePage(5), "reference")
        .set(DocumentTypePage(6), documentTypeModel).set(DocumentReferencePage(6), "reference")
        .set(DocumentTypePage(7), documentTypeModel).set(DocumentReferencePage(7), "reference")
        .set(DocumentTypePage(8), documentTypeModel).set(DocumentReferencePage(8), "reference")
      )) {
        val req = FakeRequest(POST, controllerRoute)

        val result = testController.onSubmit(testErn, testDraftId)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Setup() {
        MockDocumentsAddToListHelper.allDocumentsSummary()

        val req = FakeRequest(POST, controllerRoute).withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val result = testController.onSubmit(testErn, testDraftId)(req)

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          formOpt = Some(boundForm),
          onSubmitCall = onSubmitCall,
          documents = Seq.empty,
          showNoOption = true
        )(dataRequest(req), messages(req)).toString
      }

      "redirect to Journey Recovery for a POST if no existing data is found" in new Setup(None) {
        val req = FakeRequest(POST, controllerRoute).withFormUrlEncodedBody(("value", DocumentsAddToList.values.head.toString))

        val result = testController.onSubmit(testErn, testDraftId)(req)

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
