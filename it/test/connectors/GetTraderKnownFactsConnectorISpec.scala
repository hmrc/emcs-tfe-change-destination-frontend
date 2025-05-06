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

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, urlEqualTo}
import com.github.tomakehurst.wiremock.http.Fault
import connectors.referenceData.GetTraderKnownFactsConnector
import models.TraderKnownFacts
import models.response.UnexpectedDownstreamResponseError
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class GetTraderKnownFactsConnectorISpec extends AnyFreeSpec
  with WireMockHelper
  with ScalaFutures
  with Matchers
  with IntegrationPatience
  with EitherValues
  with OptionValues {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  def url(ern: String) = s"/emcs-tfe/trader-known-facts?exciseRegistrationId=$ern"

  val testErn = "testErn"
  val testMinTraderKnownFacts: TraderKnownFacts = TraderKnownFacts(
    traderName = "testTraderName",
    addressLine1 = None,
    addressLine2 = None,
    addressLine3 = None,
    addressLine4 = None,
    addressLine5 = None,
    postcode = None
  )

  ".getTraderKnownFacts" - {
    def app: Application =
      new GuiceApplicationBuilder()
        .configure("microservice.services.emcs-tfe.port" -> server.port)
        .build()

    lazy val connector: GetTraderKnownFactsConnector = app.injector.instanceOf[GetTraderKnownFactsConnector]

    "must return Right(Seq[TraderKnownFacts]) when the server responds OK" in {

      server.stubFor(
        get(urlEqualTo(url(testErn)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.obj("traderName" -> "testTraderName"))))
      )

      connector.getTraderKnownFacts(testErn).futureValue mustBe Right(Some(testMinTraderKnownFacts))
    }

    "must return Right(None) when the server responds NO_CONTENT" in {

      server.stubFor(
        get(urlEqualTo(url(testErn)))
          .willReturn(aResponse().withStatus(NO_CONTENT))
      )

      connector.getTraderKnownFacts(testErn).futureValue mustBe Right(None)
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        get(urlEqualTo(url(testErn)))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getTraderKnownFacts(testErn).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        get(urlEqualTo(url(testErn)))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getTraderKnownFacts(testErn).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
