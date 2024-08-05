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
import models.response.{NRSBrokerJsonParsingError, UnexpectedDownstreamResponseError}
import play.api.http.Status
import play.api.libs.json.{Json, JsonValidationError}
import uk.gov.hmrc.http.HttpResponse

class NRSBrokerHttpParserSpec extends SpecBase with NRSBrokerHttpParser with NRSBrokerFixtures {

  "NRSBrokerReads.read(method: String, url: String, response: HttpResponse)" - {

    "should return Right[NRSBrokerInsertPayloadResponse]" - {

      s"when an ACCEPTED (${Status.ACCEPTED}) response is retrieved" in {
        NRSBrokerReads.read("", "", HttpResponse(Status.ACCEPTED, json = Json.obj("reference" -> "ref1"), headers = Map.empty)) mustBe Right(nrsBrokerResponseModel)
      }
    }

    "should return Left[NRSBrokerJsonParsingError]" - {

      "when the response body is not JSON" in {

        val errorMessage = "Unexpected character ('<' (code 60)): expected a valid value (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\n at [Source: (String)\"<Error>wow</Error>\"; line: 1, column: 2]"

        NRSBrokerReads.read("", "", HttpResponse(Status.ACCEPTED, body = "<Error>wow</Error>")) mustBe Left(NRSBrokerJsonParsingError(Seq(JsonValidationError(errorMessage))))
      }

      "when the response body is JSON but not in a valid format" in {

        NRSBrokerReads.read("", "", HttpResponse(Status.ACCEPTED, json = Json.obj("wow" -> "eee"), headers = Map.empty)) mustBe Left(NRSBrokerJsonParsingError(Seq(JsonValidationError("error.path.missing"))))
      }
    }

    "should return Left[UnexpectedDownstreamError]" - {

      "when status is anything else" in {
        NRSBrokerReads.read("", "", HttpResponse(Status.INTERNAL_SERVER_ERROR, "")) mustBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }
}