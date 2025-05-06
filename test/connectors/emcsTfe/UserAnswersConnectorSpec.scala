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

package connectors.emcsTfe

import base.SpecBase
import mocks.connectors.MockHttpClient
import models.response.{JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import scala.concurrent.{ExecutionContext, Future}

class UserAnswersConnectorSpec extends SpecBase with Status with MimeTypes with HeaderNames with MockHttpClient {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  lazy val connector = new UserAnswersConnectorImpl(mockHttpClient, appConfig)

  ".get()" - {

    "should return a successful response" - {

      "when downstream call is successful and returns some JSON" in {

        MockHttpClient.get(url"${appConfig.emcsTfeBaseUrl}/user-answers/change-destination/$testErn/$testArc")
          .returns(Future.successful(Right(Some(emptyUserAnswers))))

        connector.get(testErn, testArc).futureValue mustBe Right(Some(emptyUserAnswers))
      }

      "when downstream call is successful and returns None" in {

        MockHttpClient.get(url"${appConfig.emcsTfeBaseUrl}/user-answers/change-destination/$testErn/$testArc")
          .returns(Future.successful(Right(None)))

        connector.get(testErn, testArc).futureValue mustBe Right(None)
      }
    }

    "should return an error response" - {

      "when downstream call fails" in {

        MockHttpClient.get(url"${appConfig.emcsTfeBaseUrl}/user-answers/change-destination/$testErn/$testArc")
          .returns(Future.successful(Left(JsonValidationError)))

        connector.get(testErn, testArc).futureValue mustBe Left(JsonValidationError)
      }
    }
  }

  ".put()" - {

    "should return a successful response" - {

      "when downstream call is successful and returns some JSON" in {

        MockHttpClient.put(
          url = url"${appConfig.emcsTfeBaseUrl}/user-answers/change-destination/$testErn/$testArc",
          body = Json.toJson(emptyUserAnswers)
        ).returns(Future.successful(Right(emptyUserAnswers)))

        connector.put(emptyUserAnswers).futureValue mustBe Right(emptyUserAnswers)
      }
    }

    "should return an error response" - {

      "when downstream call fails" in {

        MockHttpClient.put(
          url = url"${appConfig.emcsTfeBaseUrl}/user-answers/change-destination/$testErn/$testArc",
          body = Json.toJson(emptyUserAnswers)
        ).returns(Future.successful(Left(JsonValidationError)))

        connector.put(emptyUserAnswers).futureValue mustBe Left(JsonValidationError)
      }
    }
  }

  ".delete()" - {

    "should return a successful response" - {

      "when downstream call is successful" in {

        MockHttpClient.delete(
          url = url"${appConfig.emcsTfeBaseUrl}/user-answers/change-destination/$testErn/$testArc"
        ).returns(Future.successful(Right(true)))

        connector.delete(testErn, testArc).futureValue mustBe Right(true)
      }
    }

    "should return an error response" - {

      "when downstream call fails" in {

        MockHttpClient.delete(
          url = url"${appConfig.emcsTfeBaseUrl}/user-answers/change-destination/$testErn/$testArc"
        ).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        connector.delete(testErn, testArc).futureValue mustBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }
}
