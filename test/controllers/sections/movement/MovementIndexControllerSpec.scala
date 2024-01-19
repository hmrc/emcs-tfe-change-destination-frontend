/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.sections.movement

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import mocks.services.MockUserAnswersService
import models.UserAnswers
import models.sections.ReviewAnswer.KeepAnswers
import pages.sections.movement.MovementReviewPage
import play.api.http.Status.SEE_OTHER
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}

class MovementIndexControllerSpec extends SpecBase with MockUserAnswersService {

  class Test(userAnswers: Option[UserAnswers]) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new MovementIndexController(
      userAllowList = fakeUserAllowListAction,
      auth = fakeAuthAction,
      getData = new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      requireData = dataRequiredAction,
      withMovement = new FakeMovementAction(maxGetMovementResponse.copy(eadEsad = maxGetMovementResponse.eadEsad.copy(invoiceDate = Some("2023-01-01")))),
      controllerComponents = Helpers.stubMessagesControllerComponents()
    )
  }

  "MovementIndexController" - {

    "must redirect to the Movement Review Answers page when the movement section needs reviewing" in new Test(Some(emptyUserAnswers)) {
      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.movement.routes.MovementReviewAnswersController.onPageLoad(testErn, testArc).url
    }

    "must redirect to the Movement Check Answers page when the movement section has been reviewed" in new Test(Some(
      emptyUserAnswers.set(MovementReviewPage, KeepAnswers)
    )) {
      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.movement.routes.MovementCheckAnswersController.onPageLoad(testErn, testArc).url
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
