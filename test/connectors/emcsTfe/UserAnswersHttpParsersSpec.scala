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
import models.UserAnswers
import models.response.{BadRequestError, JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.libs.json.{Json, Reads}
import uk.gov.hmrc.http.{HttpClient, HttpResponse}

class UserAnswersHttpParsersSpec extends SpecBase with Status with MimeTypes with HeaderNames with MockHttpClient {

  lazy val httpParser = new UserAnswersHttpParsers {
    override implicit val reads: Reads[UserAnswers] = UserAnswers.format

    override def http: HttpClient = mockHttpClient
  }

  "GetUserAnswersReads.read(method: String, url: String, response: HttpResponse)" - {

    "should return a successful response" - {

      "when valid JSON is returned that can be parsed to the model" in {

        val httpResponse = HttpResponse(Status.OK, Json.toJson(emptyUserAnswers), Map())

        httpParser.GetUserAnswersReads.read("", "", httpResponse) mustBe Right(Some(emptyUserAnswers))
      }

      "when NO_CONTENT is returned" in {

        val httpResponse = HttpResponse(Status.NO_CONTENT, "")

        httpParser.GetUserAnswersReads.read("", "", httpResponse) mustBe Right(None)
      }
    }

    "should return UnexpectedDownstreamError" - {

      s"when status is not OK (${Status.OK})" in {

        val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, Json.obj(), Map())

        httpParser.GetUserAnswersReads.read("", "", httpResponse) mustBe Left(UnexpectedDownstreamResponseError)
      }
    }

    "should return JsonValidationError" - {

      s"when response does not contain Json" in {

        val httpResponse = HttpResponse(Status.OK, "", Map())

        httpParser.GetUserAnswersReads.read("", "", httpResponse) mustBe Left(JsonValidationError)
      }

      s"when response contains JSON but can't be deserialized to model" in {

        val httpResponse = HttpResponse(Status.OK, Json.obj(), Map())

        httpParser.GetUserAnswersReads.read("", "", httpResponse) mustBe Left(JsonValidationError)
      }
    }
  }

  "PutUserAnswersReads.read(method: String, url: String, response: HttpResponse)" - {

    "should return a successful response" - {

      "when valid JSON is returned that can be parsed to the model" in {

        val httpResponse = HttpResponse(Status.OK, Json.toJson(emptyUserAnswers), Map())

        httpParser.PutUserAnswersReads.read("", "", httpResponse) mustBe Right(emptyUserAnswers)
      }
    }

    "should return BadRequest" - {

      s"when status is BAD_REQUEST (${Status.BAD_REQUEST})" in {

        val httpResponse = HttpResponse(Status.BAD_REQUEST, "errMsg")

        httpParser.PutUserAnswersReads.read("", "", httpResponse) mustBe Left(BadRequestError("errMsg"))
      }
    }

    "should return UnexpectedDownstreamError" - {

      s"when status is not OK (${Status.OK})" in {

        val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, Json.obj(), Map())

        httpParser.PutUserAnswersReads.read("", "", httpResponse) mustBe Left(UnexpectedDownstreamResponseError)
      }
    }

    "should return JsonValidationError" - {

      s"when response does not contain Json" in {

        val httpResponse = HttpResponse(Status.OK, "", Map())

        httpParser.PutUserAnswersReads.read("", "", httpResponse) mustBe Left(JsonValidationError)
      }

      s"when response contains JSON but can't be deserialized to model" in {

        val httpResponse = HttpResponse(Status.OK, Json.obj(), Map())

        httpParser.PutUserAnswersReads.read("", "", httpResponse) mustBe Left(JsonValidationError)
      }
    }
  }

  "DeleteUserAnswersReads.read(method: String, url: String, response: HttpResponse)" - {

    "should return a successful response" - {

      "when NO_CONTENT is returned" in {

        val httpResponse = HttpResponse(Status.NO_CONTENT, "")

        httpParser.DeleteUserAnswersReads.read("", "", httpResponse) mustBe Right(true)
      }
    }

    "should return UnexpectedDownstreamError" - {

      s"when status is not OK (${Status.OK})" in {

        val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, Json.obj(), Map())

        httpParser.DeleteUserAnswersReads.read("", "", httpResponse) mustBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }
}
