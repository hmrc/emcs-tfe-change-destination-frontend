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

package controllers.sections.importInformation

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import pages.sections.importInformation._
import play.api.http.Status.SEE_OTHER
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}

class ImportInformationIndexControllerSpec extends SpecBase with MockUserAnswersService {

  class Test(userAnswers: Option[UserAnswers]) {

    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new ImportInformationIndexController(
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      Helpers.stubMessagesControllerComponents()
    )
  }

  "ImportInformationIndexController" - {
    "when ImportInformationSection.isCompleted" - {
      "must redirect to the CYA controller" in new Test(Some(
        emptyUserAnswers.set(ImportCustomsOfficeCodePage, "")
      )) {
        val result = controller.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.sections.importInformation.routes.CheckYourAnswersImportController.onPageLoad(testErn, testDraftId).url)
      }
    }
    "must redirect to the Import Customs Office Code controller" in new Test(Some(emptyUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(routes.ImportCustomsOfficeCodeController.onPageLoad(testErn, testDraftId, NormalMode).url)
    }
  }
}
