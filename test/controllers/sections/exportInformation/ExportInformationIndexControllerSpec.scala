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

package controllers.sections.exportInformation

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import mocks.services.MockUserAnswersService
import models.sections.ReviewAnswer.{ChangeAnswers, KeepAnswers}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeExportInformationNavigator
import pages.sections.exportInformation.{ExportCustomsOfficePage, ExportInformationReviewPage}
import play.api.http.Status.SEE_OTHER
import play.api.test.FakeRequest
import play.api.test.Helpers._

class ExportInformationIndexControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val request = FakeRequest(GET, controllers.sections.exportInformation.routes.ExportInformationIndexController.onPageLoad(testErn, testArc).url)

    lazy val testController = new ExportInformationIndexController(
      mockUserAnswersService,
      new FakeExportInformationNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse. copy(deliveryPlaceCustomsOfficeReferenceNumber = None)),
      fakeUserAllowListAction,
      messagesControllerComponents
    )

  }

  "ExportInformationIndexController" - {
    "when ExportInformationSection.isCompleted" - {
      "must redirect to the CYA controller" in new Fixture(Some(emptyUserAnswers
        .set(ExportCustomsOfficePage, "")
        .set(ExportInformationReviewPage, KeepAnswers)
      )) {
        val result = testController.onPageLoad(testErn, testArc)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.sections.exportInformation.routes.ExportInformationCheckAnswersController.onPageLoad(testErn, testArc).url)
      }
    }

    "when ExportInformationSection.needsReview" - {
      "must redirect to the CYA controller" in new Fixture(Some(
        emptyUserAnswers
          .set(ExportCustomsOfficePage, "")
      )) {
        val result = testController.onPageLoad(testErn, testArc)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.sections.exportInformation.routes.ExportInformationCheckAnswersController.onPageLoad(testErn, testArc).url)
      }
    }

    "when there the section is not completed or needs review" - {

      "must redirect to the export customs office controller" in new Fixture(
        Some(emptyUserAnswers.set(ExportInformationReviewPage, ChangeAnswers))
      ) {
        val result = testController.onPageLoad(testErn, testArc)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.sections.exportInformation.routes.ExportCustomsOfficeController.onPageLoad(testErn, testArc, NormalMode).url)
      }
    }
  }
}
