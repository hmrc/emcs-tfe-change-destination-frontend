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
import fixtures.ItemFixtures
import mocks.connectors.MockWineOperatorsConnector
import models.response.{ReferenceDataException, UnexpectedDownstreamResponseError}
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetWineOperatorsServiceSpec extends SpecBase with MockWineOperatorsConnector with ItemFixtures {

  implicit val hc = HeaderCarrier()
  implicit val ec = ExecutionContext.global

  lazy val testService = new GetWineOperationsService(mockGetWineOperationsConnector)

  ".getWineOperations" - {

    "should return Seq[WineOperators]" - {

      "when Connector returns success from downstream" in {

        val expectedResult = testWineOperations

        MockWineOperatorsConnector.getWineOperations().returns(Future(Right(expectedResult)))

        val actualResults = testService.getWineOperations().futureValue

        actualResults mustBe expectedResult
      }
    }

    "should throw ReferenceDataException" - {

      "when Connector returns failure from downstream" in {

        val expectedResult = "Invalid response from wine operations endpoint"

        MockWineOperatorsConnector.getWineOperations().returns(Future(Left(UnexpectedDownstreamResponseError)))

        val actualResult = intercept[ReferenceDataException](await(testService.getWineOperations())).getMessage

        actualResult mustBe expectedResult
      }
    }
  }
}
