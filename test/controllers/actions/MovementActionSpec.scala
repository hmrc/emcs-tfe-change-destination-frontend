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

package controllers.actions

import base.SpecBase
import mocks.connectors.MockGetMovementConnector
import models.requests.{MovementRequest, UserRequest}
import models.response.emcsTfe.GetMovementResponse
import models.response.{ErrorResponse, UnexpectedDownstreamResponseError}
import play.api.mvc.Result
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class MovementActionSpec extends SpecBase with MockGetMovementConnector {

  implicit val hc = HeaderCarrier()
  implicit lazy val request = UserRequest(FakeRequest(), testErn, testInternalId, testCredId, testSessionId, false)
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  lazy val movementAction = new MovementActionImpl(
    getMovementConnector = mockGetMovementConnector
  )

  lazy val fakeBlock: MovementRequest[_] => Future[Result] = {
    _: MovementRequest[_] => Future.successful(Ok(""))
  }

  class Harness(forceFetchNew: Boolean, connectorResponse: Either[ErrorResponse, GetMovementResponse]) {

    MockGetMovementConnector.getMovement(testErn, testArc, forceFetchNew)
      .returns(Future.successful(connectorResponse))

    lazy val result: Future[Result] = movementAction.apply(testArc, forceFetchNew).invokeBlock(request, fakeBlock)
  }

  "MovementAction" - {
    ".apply" - {
      "should return Left(_)" - {
        "the connector returns an error and redirect to the wrong ARC page" in new Harness(true, connectorResponse = Left(UnexpectedDownstreamResponseError)) {
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.error.routes.ErrorController.wrongArc().url)
        }
      }

      "should return Right(_)" - {
        "the connector returns OK" in new Harness(true, connectorResponse = Right(maxGetMovementResponse)) {
          status(result) mustBe OK
        }
      }
    }

    ".upToDate" - {
      "should call the connector to fetch an updated movement and return the result" in new Harness(true, connectorResponse = Right(maxGetMovementResponse)) {
        override lazy val result = movementAction.upToDate(testArc).invokeBlock(request, fakeBlock)
        status(result) mustBe OK
      }
    }

    ".fromCache" - {
      "should call the connector to fetch a cached movement and return the result" in new Harness(false, connectorResponse = Right(maxGetMovementResponse)) {
        override lazy val result = movementAction.fromCache(testArc).invokeBlock(request, fakeBlock)
        status(result) mustBe OK
      }
    }
  }
}
