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
import config.AppConfig
import controllers.actions.UserAllowListActionImpl
import featureswitch.core.config.{FeatureSwitching, UserAllowList}
import handlers.ErrorHandler
import mocks.connectors.MockUserAllowListConnector
import models.requests.{CheckUserAllowListRequest, UserRequest}
import models.{ErrorResponse, UnexpectedDownstreamResponseError}
import org.scalamock.scalatest.MockFactory
import play.api.mvc.Result
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class UserAllowListActionSpec extends SpecBase with MockFactory with MockUserAllowListConnector with FeatureSwitching {

  lazy val app = applicationBuilder(userAnswers = None).build()
  implicit val hc = HeaderCarrier()
  implicit lazy val request = UserRequest(FakeRequest(), testErn, testInternalId, testCredId, false)

  lazy val errorHandler = app.injector.instanceOf[ErrorHandler]
  override lazy val config = mock[AppConfig]

  lazy val userAllowListAction = new UserAllowListActionImpl(
    userAllowListConnector = mockUserAllowListConnector,
    errorHandler = errorHandler,
    config = config
  )

  class Harness(enabled: Boolean, connectorResponse: Either[ErrorResponse, Boolean]) {

    if (enabled) {
      enable(UserAllowList)
      MockUserAllowListConnector.check(CheckUserAllowListRequest(testErn))
        .returns(Future.successful(connectorResponse))
    } else {
      disable(UserAllowList)
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

        "must return SEE_OTHER and redirect to the not on private beta page" in new Harness(enabled = true, connectorResponse = Right(false)) {
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.error.routes.ErrorController.notOnPrivateBeta().url)
        }
      }

      "when the connector returns a Left" - {
        "render ISE" in new Harness(enabled = true, connectorResponse = Left(UnexpectedDownstreamResponseError)) {
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
