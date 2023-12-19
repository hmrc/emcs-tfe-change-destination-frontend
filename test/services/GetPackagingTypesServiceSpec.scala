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
import mocks.connectors.{MockGetBulkPackagingTypesConnector, MockGetItemPackagingTypesConnector}
import fixtures.ItemFixtures
import models.response.{PackagingTypesException, UnexpectedDownstreamResponseError}
import models.sections.items.ItemBulkPackagingCode
import models.sections.items.ItemBulkPackagingCode.{BulkGas, BulkLiquefiedGas, BulkLiquid}
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetPackagingTypesServiceSpec extends SpecBase
  with MockGetBulkPackagingTypesConnector
  with MockGetItemPackagingTypesConnector
  with ItemFixtures {

  implicit val hc = HeaderCarrier()
  implicit val ec = ExecutionContext.global

  lazy val testService = new GetPackagingTypesService(mockGetBulkPackagingTypesConnector, mockGetItemPackagingTypesConnector)

  val packagingCodes: Seq[ItemBulkPackagingCode] = Seq(BulkGas, BulkLiquefiedGas, BulkLiquid)

  ".getBulkPackagingTypes" - {

    "should return Seq[BulkPackagingType]" - {

      "when Connector returns success from downstream" in {

        val expectedResult = bulkPackagingTypes

        MockGetBulkPackagingTypesConnector.getBulkPackagingTypes().returns(Future.successful(Right(bulkPackagingTypes)))

        val actualResults = testService.getBulkPackagingTypes(packagingCodes).futureValue

        actualResults mustBe expectedResult
      }
    }

    "should throw PackagingTypesException" - {

      "when Connector returns failure from downstream" in {

        val expectedResult = "Invalid response from packaging types code endpoint"

        MockGetBulkPackagingTypesConnector.getBulkPackagingTypes().returns(Future(Left(UnexpectedDownstreamResponseError)))

        val actualResult = intercept[PackagingTypesException](await(testService.getBulkPackagingTypes(packagingCodes))).getMessage

        actualResult mustBe expectedResult
      }
    }

  }

  ".getItemPackagingTypes" - {

    "should return Seq[ItemPackaging]" - {

      "when Connector returns success from downstream" in {

        val expectedResult = testItemPackagingTypes

        MockGetItemPackagingTypesConnector.getItemPackagingTypes().returns(Future.successful(Right(testItemPackagingTypes)))

        val actualResults = testService.getItemPackagingTypes(Some(true)).futureValue

        actualResults mustBe expectedResult
      }
    }

    "should throw PackagingTypesException" - {

      "when Connector returns failure from downstream" in {

        val expectedResult = "Invalid response from packaging types code endpoint"

        MockGetItemPackagingTypesConnector.getItemPackagingTypes().returns(Future(Left(UnexpectedDownstreamResponseError)))

        val actualResult = intercept[PackagingTypesException](await(testService.getItemPackagingTypes(None))).getMessage

        actualResult mustBe expectedResult
      }
    }

  }
}
