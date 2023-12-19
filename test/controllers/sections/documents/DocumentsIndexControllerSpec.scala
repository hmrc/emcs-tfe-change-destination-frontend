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
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeDocumentsNavigator
import pages.sections.documents.{DocumentReferencePage, DocumentsCertificatesPage, ReferenceAvailablePage}
import play.api.http.Status.SEE_OTHER
import play.api.test.FakeRequest
import play.api.test.Helpers._

class DocumentsIndexControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val request = FakeRequest(GET, routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url)

    lazy val testController = new DocumentsIndexController(
      mockUserAnswersService,
      new FakeDocumentsNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      messagesControllerComponents
    )
  }

  "DocumentsIndexController" - {
    "when DocumentsCertificate is false" - {
      "must redirect to the DocumentsCheckAnswers page" in new Fixture(Some(emptyUserAnswers.set(DocumentsCertificatesPage, false))) {
        val result = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.DocumentsCheckAnswersController.onPageLoad(testErn, testDraftId).url)
      }
    }

    "when DocumentsCertificate is true" - {
      "when DocumentsCount is > 0" - {
        "must redirect to the DocumentReference page" in new Fixture(Some(
          emptyUserAnswers
            .set(DocumentsCertificatesPage, true)
            .set(ReferenceAvailablePage(0), true)
            .set(DocumentReferencePage(0), "reference"))) {

          val result = testController.onPageLoad(testErn, testDraftId)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.DocumentsAddToListController.onPageLoad(testErn, testDraftId).url)
        }
      }

      "when DocumentsCount is 0" - {
        "must redirect to the DocumentReference page" in new Fixture(Some(emptyUserAnswers.set(DocumentsCertificatesPage, true))) {
          val result = testController.onPageLoad(testErn, testDraftId)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.DocumentsCertificatesController.onPageLoad(testErn, testDraftId, NormalMode).url)
        }
      }
    }

    "when DocumentsCertificate is not answered" - {
      "must redirect to the DocumentsCheckAnswers page" in new Fixture() {
        val result = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.DocumentsCertificatesController.onPageLoad(testErn, testDraftId, NormalMode).url)
      }
    }
  }
}
