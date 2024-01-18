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

package controllers.sections.firstTransporter

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import mocks.services.MockUserAnswersService
import models.sections.ReviewAnswer.{ChangeAnswers, KeepAnswers}
import models.{NormalMode, UserAddress, UserAnswers}
import navigation.FakeNavigators.FakeFirstTransporterNavigator
import pages.sections.firstTransporter.{FirstTransporterAddressPage, FirstTransporterNamePage, FirstTransporterReviewPage, FirstTransporterVatPage}
import play.api.http.Status.SEE_OTHER
import play.api.test.FakeRequest
import play.api.test.Helpers._

class FirstTransporterIndexControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    val request = FakeRequest(GET, controllers.sections.firstTransporter.routes.FirstTransporterIndexController.onPageLoad(testErn, testArc).url)

    lazy val testController = new FirstTransporterIndexController(
      mockUserAnswersService,
      new FakeFirstTransporterNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse.copy(firstTransporterTrader = None)),
      fakeUserAllowListAction,
      messagesControllerComponents
    )

  }

  "FirstTransporterIndexController" - {
    "when FirstTransporterSection.isCompleted" - {
      "must redirect to the CYA controller" in new Fixture(Some(
        emptyUserAnswers
          .set(FirstTransporterNamePage, "")
          .set(FirstTransporterVatPage, "")
          .set(FirstTransporterAddressPage, UserAddress(None, "", "", ""))
          .set(FirstTransporterReviewPage, KeepAnswers))) {

        val result = testController.onPageLoad(testErn, testArc)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.sections.firstTransporter.routes.FirstTransporterCheckAnswersController.onPageLoad(testErn, testArc).url)
      }

    }

    "when FirstTransporterSection.needsReview" - {
      "must redirect to the Review controller" in new Fixture(Some(
        emptyUserAnswers
          .set(FirstTransporterNamePage, "")
          .set(FirstTransporterVatPage, "")
          .set(FirstTransporterAddressPage, UserAddress(None, "", "", "")))) {

        val result = testController.onPageLoad(testErn, testArc)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.sections.firstTransporter.routes.FirstTransporterReviewController.onPageLoad(testErn, testArc).url)
      }
    }

    "when the section isn't complete or is in review state" - {
      "must redirect to the first transporter name controller" in new Fixture(
        Some(emptyUserAnswers.set(FirstTransporterReviewPage, ChangeAnswers))
      ) {
        val result = testController.onPageLoad(testErn, testArc)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.sections.firstTransporter.routes.FirstTransporterNameController.onPageLoad(testErn, testArc, NormalMode).url)
      }
    }
  }

}
