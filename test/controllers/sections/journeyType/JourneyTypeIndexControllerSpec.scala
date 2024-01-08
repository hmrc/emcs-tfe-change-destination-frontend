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

package controllers.sections.journeyType

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import mocks.services.MockUserAnswersService
import models.sections.ReviewAnswer.{ChangeAnswers, KeepAnswers}
import models.sections.journeyType.HowMovementTransported.SeaTransport
import models.{NormalMode, UserAnswers}
import navigation.JourneyTypeNavigator
import pages.sections.journeyType.{GiveInformationOtherTransportPage, HowMovementTransportedPage, JourneyTimeDaysPage, JourneyTypeReviewPage}
import play.api.http.Status.SEE_OTHER
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}

class JourneyTypeIndexControllerSpec extends SpecBase with MockUserAnswersService {

  class Test(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new JourneyTypeIndexController(
      mockUserAnswersService,
      app.injector.instanceOf[JourneyTypeNavigator],
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse),
      fakeUserAllowListAction,
      Helpers.stubMessagesControllerComponents()
    )
  }

  "JourneyTypeIndexController" - {

    "when JourneyTypeSection.isCompleted" - {
      "must redirect to the CYA controller" in new Test(Some(
        emptyUserAnswers
          .set(HowMovementTransportedPage, SeaTransport)
          .set(GiveInformationOtherTransportPage, "information")
          .set(JourneyTimeDaysPage, 1)
          .set(JourneyTypeReviewPage, KeepAnswers)
      )) {
        val result = controller.onPageLoad(testErn, testArc)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.sections.journeyType.routes.CheckYourAnswersJourneyTypeController.onPageLoad(testErn, testArc).url)
      }
    }

    "when JourneyTypeSection.needsReview" - {
      "must redirect to the CYA controller" in new Test(Some(
        emptyUserAnswers
          .set(HowMovementTransportedPage, SeaTransport)
          .set(GiveInformationOtherTransportPage, "information")
          .set(JourneyTimeDaysPage, 1)
      )) {
        val result = controller.onPageLoad(testErn, testArc)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.sections.journeyType.routes.CheckYourAnswersJourneyTypeController.onPageLoad(testErn, testArc).url)
      }
    }

    "when there the section is not completed or needs review" - {

      "must redirect to the how movement transported controller" in new Test(Some(
        emptyUserAnswers.set(JourneyTypeReviewPage, ChangeAnswers)
      )) {
        val result = controller.onPageLoad(testErn, testArc)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.sections.journeyType.routes.HowMovementTransportedController.onPageLoad(testErn, testArc, NormalMode).url)
      }
    }
  }
}
