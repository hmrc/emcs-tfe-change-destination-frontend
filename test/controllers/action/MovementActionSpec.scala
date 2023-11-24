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

package controllers.action

import base.SpecBase
import controllers.actions.MovementActionImpl
import handlers.ErrorHandler
import mocks.connectors.MockGetMovementConnector
import models.requests.{MovementRequest, UserRequest}
import models.response.emcsTfe.GetMovementResponse
import models.{ErrorResponse, JsonValidationError}
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.{OK, SEE_OTHER}
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MovementActionSpec extends SpecBase with MockitoSugar with MockGetMovementConnector {

  lazy val app = applicationBuilder(userAnswers = None).build()
  implicit val hc = HeaderCarrier()
  implicit lazy val request = UserRequest(FakeRequest(), testErn, testInternalId, testCredId, false)

  lazy val errorHandler = app.injector.instanceOf[ErrorHandler]

  lazy val movementAction = new MovementActionImpl(
    mockGetMovementConnector,
    errorHandler
  )

  class Harness(forceFetchNew: Boolean, connectorResponse: Either[ErrorResponse, GetMovementResponse]) {

    val action = if(forceFetchNew) movementAction.upToDate(testArc) else movementAction.fromCache(testArc)

    MockGetMovementConnector.getMovement(testErn, testArc, forceFetchNew).returns(Future.successful(connectorResponse))

    val result = action.invokeBlock(request, { _: MovementRequest[_] =>
      Future.successful(Ok)
    })
  }

  "MovementAction" - {

    ".fromCache() - (forceFetchNew=false)" - {

      "when the connector returns a Movement successfully for the requested ARC" - {

        "must execute the supplied block" in new Harness(
          forceFetchNew = false,
          connectorResponse = Right(getMovementResponseModel)
        ) {
          status(result) mustBe OK
        }
      }

      "when the connector does not return any data" - {

        "must render a BadRequest" in new Harness(
          forceFetchNew = true,
          connectorResponse = Left(JsonValidationError)
        ) {
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.error.routes.ErrorController.wrongArc().url)
        }
      }
    }

    ".upToDate() - (forceFetchNew=true)" - {

      "when the connector returns a Movement successfully for the requested ARC" - {

        "must execute the supplied block" in new Harness(
          forceFetchNew = true,
          connectorResponse = Right(getMovementResponseModel)
        ) {
          status(result) mustBe OK
        }
      }

      "when the connector does not return any data" - {

        "must render a BadRequest" in new Harness(
          forceFetchNew = true,
          connectorResponse = Left(JsonValidationError)
        ) {
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.error.routes.ErrorController.wrongArc().url)
        }
      }
    }
  }
}
