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
import config.AppConfig
import mocks.connectors.MockUserAllowListConnector
import models.requests.{CheckUserAllowListRequest, UserRequest}
import models.response.{ErrorResponse, UnexpectedDownstreamResponseError}
import org.scalamock.scalatest.MockFactory
import play.api.mvc.Result
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserAllowListActionSpec extends SpecBase with MockFactory with MockUserAllowListConnector {

  implicit val hc = HeaderCarrier()
  implicit lazy val request = UserRequest(FakeRequest(), testErn, testInternalId, testCredId, testSessionId, false)

  lazy val mockAppConfig = mock[AppConfig]

  lazy val userAllowListAction = new UserAllowListActionImpl(
    userAllowListConnector = mockUserAllowListConnector,
    errorHandler = errorHandler,
    config = mockAppConfig
  )

  class Harness(enabled: Boolean, connectorResponse: Either[ErrorResponse, Boolean]) {

    (() => mockAppConfig.allowListEnabled).expects().returns(enabled).anyNumberOfTimes()

    if (enabled) {
      MockUserAllowListConnector.check(CheckUserAllowListRequest(testErn))
        .returns(Future.successful(connectorResponse))
    }

    val result: Future[Result] = userAllowListAction.invokeBlock(request, { _: UserRequest[_] =>
      Future.successful(Ok)
    })
  }

  "UserAllowListAction" - {

    "when the allow list feature is enabled" - {

      "when the connector returns true (on the list)" - {

        "must execute the supplied block" in new Harness(enabled = true, connectorResponse = Right(true)) {
          status(result) mustBe OK
        }
      }

      "when the connector returns false (NOT on the list)" - {

        "must execute the supplied block" in new Harness(enabled = true, connectorResponse = Right(false)) {
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.error.routes.ErrorController.notOnPrivateBeta().url)
        }
      }

      "when the connector returns a Left" - {
        "must execute the supplied block" in new Harness(enabled = true, connectorResponse = Left(UnexpectedDownstreamResponseError)) {
          status(result) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }

    "when the allow list feature is disabled" - {

      "must execute the supplied block" in new Harness(enabled = false, connectorResponse = Right(false)) {
        status(result) mustBe OK
      }
    }
  }
}
