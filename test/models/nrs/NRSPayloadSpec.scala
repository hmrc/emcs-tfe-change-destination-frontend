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

package models.nrs

import base.SpecBase
import fixtures.NRSBrokerFixtures
import models.requests.DataRequest
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.http.{Authorization, HeaderCarrier}

import java.time.Instant

class NRSPayloadSpec extends SpecBase with NRSBrokerFixtures {

  val hc: HeaderCarrier = HeaderCarrier()
  implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

  ".apply" - {

    "should generate the correct payload by encoding, hashing the payload and applying the correct attributes" in {

      val result = NRSPayload.apply(testPlainTextPayload, identityDataModel, testErn, Instant.ofEpochMilli(1L))(hc.copy(authorization = Some(Authorization(testAuthToken))), request)

      result mustBe nrsPayloadModel.copy(metadata = nrsPayloadModel.metadata.copy(headerData = Json.obj("Host" -> "localhost")))
    }

    "should throw an exception when an auth token is not in the HeaderCarrier" in {

      intercept[NoSuchElementException](NRSPayload.apply(testPlainTextPayload, identityDataModel, testErn, Instant.ofEpochMilli(1L))(hc.copy(authorization = None), request))
    }
  }

  ".writes" - {

    "generate the correct JSON" in {

      Json.toJson(nrsPayloadModel)(NRSPayload.writes) mustBe nrsPayloadJson
    }
  }
}
