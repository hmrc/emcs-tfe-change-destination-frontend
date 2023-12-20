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

package services

import base.SpecBase
import mocks.connectors.MockGetCountriesAndMemberStatesConnector
import mocks.services.MockGetMemberStatesService
import models.CountryModel
import models.response.{CountriesAndMemberStatesException, UnexpectedDownstreamResponseError}
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetCountriesAndMemberStatesServiceSpec extends SpecBase with MockGetCountriesAndMemberStatesConnector with MockGetMemberStatesService {

  implicit val hc = HeaderCarrier()
  implicit val ec = ExecutionContext.global

  lazy val testService = new GetCountriesAndMemberStatesService(mockGetCountriesAndMemberStatesConnector, mockGetMemberStatesService)

  ".getCountryCodesAndMemberStates" - {

    "should return Seq[CountryModel]" - {

      "when Connector returns success from downstream" in {

        val expectedResult = Seq(
          countryModelAT,
          countryModelBE
        )

        MockGetCountriesAndMemberStatesConnector.getCountryCodesAndMemberStates().returns(Future(Right(Seq(countryModelAT, countryModelBE))))

        val actualResults = testService.getCountryCodesAndMemberStates().futureValue

        actualResults mustBe expectedResult
      }
    }

    "should throw CountriesAndMemberStatesException" - {

      "when Connector returns failure from downstream" in {

        val expectedResult = "No countries retrieved"

        MockGetCountriesAndMemberStatesConnector.getCountryCodesAndMemberStates().returns(Future(Left(UnexpectedDownstreamResponseError)))

        val actualResult = intercept[CountriesAndMemberStatesException](await(testService.getCountryCodesAndMemberStates())).getMessage

        actualResult mustBe expectedResult
      }
    }
  }

  ".removeEUMemberStates" - {

    "should retrieve EU member states and remove them from the existing list" - {

      "when EU member states connector returns success from downstream" in {

        val countryModelEl = CountryModel(
          countryCode = "EL",
          country = "Greece"
        )

        val countryModelGr = CountryModel(
          countryCode = "GR",
          country = "Greece"
        )

        val expectedResult = Seq(
          countryModelAU,
          countryModelBR,
          countryModelGB
        )

        MockGetMemberStatesService.getMemberStates().returns(Future(Seq(countryModelAT, countryModelBE, countryModelGB, countryModelEl)))

        val actualResults = testService.removeEUMemberStates(Seq(countryModelAT, countryModelBE, countryModelGB, countryModelAU, countryModelBR, countryModelEl, countryModelGr)).futureValue

        actualResults mustBe expectedResult
      }
    }
  }
}
