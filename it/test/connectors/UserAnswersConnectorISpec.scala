/*
 * Copyright 2025 HM Revenue & Customs
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

package test.connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import connectors.emcsTfe.UserAnswersConnector
import models.UserAnswers
import models.response.UnexpectedDownstreamResponseError
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues}
import play.api.Application
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, NO_CONTENT, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.AUTHORIZATION
import uk.gov.hmrc.http.HeaderCarrier

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.concurrent.ExecutionContext.Implicits.global

class UserAnswersConnectorISpec extends AnyFreeSpec
  with WireMockHelper
  with ScalaFutures
  with Matchers
  with IntegrationPatience
  with EitherValues
  with OptionValues {

  val testErn: String = "ern"
  val testDraftId: String = "arc"

  val emptyUserAnswers: UserAnswers = UserAnswers(
    ern = testErn,
    arc = testDraftId,
    validationErrors = Seq.empty,
    lastUpdated = Instant.now().truncatedTo(ChronoUnit.MILLIS)
  )

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.emcs-tfe.port" -> server.port,
        "internal-auth.token" -> "token"
      )
      .build()

  val url = s"/emcs-tfe/user-answers/change-destination/ern/arc"

  private lazy val connector: UserAnswersConnector = app.injector.instanceOf[UserAnswersConnector]

  ".get" - {
    val response = aResponse().withBody(Json.stringify(Json.toJson(emptyUserAnswers)))
    "must return true when the server responds OK" in {

      server.stubFor(
        get(urlEqualTo(url))
          .willReturn(response.withStatus(OK))
      )

      connector.get(testErn, testDraftId).futureValue mustBe Right(Some(emptyUserAnswers))
    }

    "must return false when the server responds NOT_FOUND" in {

      server.stubFor(
        get(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      connector.get(testErn, testDraftId).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        get(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.get(testErn, testDraftId).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        get(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.get(testErn, testDraftId).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }

  ".put" - {
    val body = Json.toJson(emptyUserAnswers)

    "must return true when the server responds OK" in {

      server.stubFor(
        put(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(body)))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(body)))
      )

      connector.put(emptyUserAnswers).futureValue mustBe Right(emptyUserAnswers)
    }

    "must return false when the server responds NOT_FOUND" in {

      server.stubFor(
        put(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(body)))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      connector.put(emptyUserAnswers).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        put(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(body)))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.put(emptyUserAnswers).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        put(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .withRequestBody(equalToJson(Json.stringify(body)))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.put(emptyUserAnswers).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }

  ".delete" - {

    "must return true when the server responds OK" in {

      server.stubFor(
        delete(urlEqualTo(url))
          .willReturn(aResponse().withStatus(NO_CONTENT))
      )

      connector.delete(testErn, testDraftId).futureValue mustBe Right(true)
    }

    "must return false when the server responds NOT_FOUND" in {

      server.stubFor(
        delete(urlEqualTo(url))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      connector.delete(testErn, testDraftId).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        delete(urlEqualTo(url))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.delete(testErn, testDraftId).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        delete(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.delete(testErn, testDraftId).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
