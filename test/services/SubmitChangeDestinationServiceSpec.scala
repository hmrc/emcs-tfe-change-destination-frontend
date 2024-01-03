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
import fixtures.SubmitChangeDestinationFixtures
import mocks.connectors.MockSubmitChangeDestinationConnector
import models.response.{SubmitChangeDestinationException, UnexpectedDownstreamResponseError}
import play.api.test.FakeRequest
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class SubmitChangeDestinationServiceSpec extends SpecBase with MockSubmitChangeDestinationConnector with SubmitChangeDestinationFixtures {

  implicit val hc = HeaderCarrier()
  implicit val ec = ExecutionContext.global

  lazy val testService = new SubmitChangeDestinationService(mockSubmitChangeDestinationConnector)

  ".submit(ern: String, submission: SubmitChangeDestinationModel)" - {

    "should return Success response" - {

      "when Connector returns success from downstream" in {

        val request = dataRequest(FakeRequest())

        MockSubmitChangeDestinationConnector.submit(minimumSubmitChangeDestinationModel).returns(Future.successful(Right(submitChangeDestinationResponseEIS)))

        testService.submit(minimumSubmitChangeDestinationModel)(request, hc).futureValue mustBe submitChangeDestinationResponseEIS
      }
    }

    "should return Failure response" - {

      "when Connector returns failure from downstream" in {

        val request = dataRequest(FakeRequest())

        MockSubmitChangeDestinationConnector.submit(minimumSubmitChangeDestinationModel).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))
        intercept[SubmitChangeDestinationException](await(testService.submit(minimumSubmitChangeDestinationModel)(request, hc))).getMessage mustBe
          s"Failed to submit Change Destination to emcs-tfe for ern: '$testErn' & arc: '$testArc'"
      }
    }
  }
}
