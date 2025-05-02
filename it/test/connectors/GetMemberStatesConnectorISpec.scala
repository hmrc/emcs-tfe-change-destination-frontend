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
import connectors.referenceData.GetMemberStatesConnector
import fixtures.BaseFixtures
import models.CountryModel
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

class GetMemberStatesConnectorISpec extends AnyFreeSpec
  with WireMockHelper
  with ScalaFutures
  with Matchers
  with IntegrationPatience
  with EitherValues
  with OptionValues
  with BaseFixtures {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  val url = "/emcs-tfe-reference-data/oracle/member-states"

  val memberStatesSeq: Seq[CountryModel] = Seq(
    countryModelAT,
    countryModelBE
  )

  ".getMemberStates" - {

    def app: Application =
      new GuiceApplicationBuilder()
        .configure("microservice.services.emcs-tfe-reference-data.port" -> server.port)
        .configure("features.stub-get-trader-known-facts" -> "false")
        .build()

    lazy val connector: GetMemberStatesConnector = app.injector.instanceOf[GetMemberStatesConnector]

    "must return Right(Seq[CountryModel]) when the server responds OK" in {

      server.stubFor(
        get(urlEqualTo(url))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.arr(countryJsonAT, countryJsonBE))))
      )

      connector.getMemberStates().futureValue mustBe Right(memberStatesSeq)
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getMemberStates().futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getMemberStates().futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
