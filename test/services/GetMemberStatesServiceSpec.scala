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
import mocks.connectors.MockGetMemberStatesConnector
import models.response.{MemberStatesException, UnexpectedDownstreamResponseError}
import play.api.test.Helpers._
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

class GetMemberStatesServiceSpec extends SpecBase with MockGetMemberStatesConnector {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  lazy val testService = new GetMemberStatesService(mockGetMemberStatesConnector)

  ".getMemberStatesSelectItems" - {

    "should return Seq[SelectItem]" - {

      "when Connector returns success from downstream" in {

        val expectedResult = Seq(
          SelectItem(Some(countryModelAT.countryCode), s"${countryModelAT.country} (${countryModelAT.countryCode})"),
          SelectItem(Some(countryModelBE.countryCode), s"${countryModelBE.country} (${countryModelBE.countryCode})")
        )

        MockGetMemberStatesConnector.getMemberStates().returns(Future(Right(Seq(countryModelAT, countryModelBE))))

        val actualResults = testService.getMemberStatesSelectItems().futureValue

        actualResults mustBe expectedResult
      }
    }

    "should throw MemberStatesException" - {

      "when Connector returns failure from downstream" in {

        val expectedResult = "No member states retrieved"

        MockGetMemberStatesConnector.getMemberStates().returns(Future(Left(UnexpectedDownstreamResponseError)))

        val actualResult = intercept[MemberStatesException](await(testService.getMemberStatesSelectItems())).getMessage

        actualResult mustBe expectedResult
      }
    }
  }

  ".getMemberStates" - {

    "should return Seq[CountryModel]" - {

      "when Connector returns success from downstream" in {

        val expectedResult = Seq(
          countryModelAT,
          countryModelBE
        )

        MockGetMemberStatesConnector.getMemberStates().returns(Future(Right(Seq(countryModelAT, countryModelBE))))

        val actualResults = testService.getMemberStates().futureValue

        actualResults mustBe expectedResult
      }
    }

    "should throw MemberStatesException" - {

      "when Connector returns failure from downstream" in {

        val expectedResult = "No member states retrieved"

        MockGetMemberStatesConnector.getMemberStates().returns(Future(Left(UnexpectedDownstreamResponseError)))

        val actualResult = intercept[MemberStatesException](await(testService.getMemberStatesSelectItems())).getMessage

        actualResult mustBe expectedResult
      }
    }
  }
}
