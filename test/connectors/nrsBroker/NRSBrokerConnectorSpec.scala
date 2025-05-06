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

package connectors.nrsBroker

import base.SpecBase
import fixtures.NRSBrokerFixtures
import mocks.config.MockAppConfig
import mocks.connectors.MockHttpClient
import models.response.UnexpectedDownstreamResponseError
import org.scalatest.BeforeAndAfterAll
import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import scala.concurrent.{ExecutionContext, Future}

class NRSBrokerConnectorSpec extends SpecBase
  with Status
  with MimeTypes
  with HeaderNames
  with MockHttpClient
  with BeforeAndAfterAll
  with NRSBrokerFixtures {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = ExecutionContext.global

  val fakeUrl = "http://localhost:99999/emcs-tfe-nrs-message-broker"

  trait Test extends MockAppConfig {
    MockAppConfig.nrsBrokerBaseUrl.returns(fakeUrl)
    lazy val connector = new NRSBrokerConnector(mockHttpClient, mockAppConfig)
  }

  ".submitPayload" - {

    "should return a successful response" - {

      "when downstream call is successful" in new Test {

        MockHttpClient.put(
          url = url"$fakeUrl/trader/$testErn/nrs/submission",
          body = Json.toJson(nrsPayloadModel)
        ).returns(Future.successful(Right(nrsBrokerResponseModel)))

        await(connector.submitPayload(nrsPayloadModel, testErn)) mustBe Right(nrsBrokerResponseModel)
      }
    }

    "return an error response" - {

      "when downstream call fails" in new Test {

        MockHttpClient.put(
          url = url"$fakeUrl/trader/$testErn/nrs/submission",
          body = Json.toJson(nrsPayloadModel)
        ).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        await(connector.submitPayload(nrsPayloadModel, testErn)) mustBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }

}
