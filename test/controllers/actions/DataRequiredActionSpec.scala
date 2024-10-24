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

package controllers.actions

import base.SpecBase
import controllers.routes
import fixtures.GetMovementResponseFixtures
import mocks.services.MockUserAnswersService
import models.requests.{DataRequest, MovementRequest, OptionalDataRequest, UserRequest}
import pages.DeclarationPage
import play.api.http.Status.SEE_OTHER
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.LOCATION

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataRequiredActionSpec extends SpecBase with MockUserAnswersService with GetMovementResponseFixtures {

  lazy val actionRefiner = new DataRequiredActionImpl {
    def callRefine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] = refine(request)
  }

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

  "DataRequiredAction" - {
    "when there are no user answers saved" - {
      "must redirect to the JourneyRecoveryController" in {
        val result: Either[Result, DataRequest[_]] = actionRefiner.callRefine(
          OptionalDataRequest(
            request = MovementRequest(
              UserRequest(fakeRequest, testErn, testInternalId, testCredId, testSessionId, hasMultipleErns = false, None),
              testArc,
              maxGetMovementResponse
            ),
            userAnswers = None,
            traderKnownFacts = None
          )
        ).futureValue

        result.isLeft mustBe true
        result.left.map(
          left => {
            left.header.status mustBe SEE_OTHER
            left.header.headers.get(LOCATION) mustBe Some(routes.JourneyRecoveryController.onPageLoad().url)
          }
        )
      }
    }

    "when there is a DeclarationPage saved" - {
      "and the current page is NOT the confirmation page" - {
        "must redirect to the not permitted page" in {

          val userAnswers = emptyUserAnswers.set(DeclarationPage, LocalDateTime.now)

          val result: Either[Result, DataRequest[_]] = actionRefiner.callRefine(
            OptionalDataRequest(
              MovementRequest(
                request = UserRequest(fakeRequest, testErn, testInternalId, testCredId, testSessionId, hasMultipleErns = false, None),
                arc = testArc,
                movementDetails = maxGetMovementResponse
              ),
              Some(userAnswers),
              Some(testMinTraderKnownFacts)
            )
          ).futureValue

          result.isLeft mustBe true
          result.left.map(
            left => {
              left.header.status mustBe SEE_OTHER
              left.header.headers.get(LOCATION) mustBe Some(routes.NotPermittedPageController.onPageLoad(testErn, testArc).url)
            }
          )
        }
      }

      "and the current page is the confirmation page" - {
        "must return a Right(DataRequest)" in {

          val fakeRequest = FakeRequest("GET", routes.ConfirmationController.onPageLoad(testErn, testArc).url)

          val userAnswers = emptyUserAnswers.set(DeclarationPage, LocalDateTime.now)

          val result: Either[Result, DataRequest[_]] = actionRefiner.callRefine(
            OptionalDataRequest(
              MovementRequest(
                request = UserRequest(fakeRequest, testErn, testInternalId, testCredId, testSessionId, hasMultipleErns = false, None),
                arc = testArc,
                movementDetails = maxGetMovementResponse
              ),
              Some(userAnswers),
              Some(testMinTraderKnownFacts)
            )
          ).futureValue

          result.isRight mustBe true
        }
      }
    }
  }

}
