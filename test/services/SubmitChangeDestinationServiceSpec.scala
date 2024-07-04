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
import mocks.services.MockAuditingService
import models.audit.SubmitChangeDestinationAudit
import models.response.UnexpectedDownstreamResponseError
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import utils.TimeMachine

import java.time.LocalDateTime
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

class SubmitChangeDestinationServiceSpec
  extends SpecBase
    with MockSubmitChangeDestinationConnector
    with SubmitChangeDestinationFixtures
    with MockAuditingService {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  val timeMachine: TimeMachine = () => LocalDateTime.parse(testReceiptDateTime)

  lazy val testService = new SubmitChangeDestinationService(mockSubmitChangeDestinationConnector, mockAuditingService, timeMachine)

  ".submit(ern: String, submission: SubmitChangeDestinationModel)" - {

    "should return a Right" - {

      "when Connector returns success from downstream" in {

        val request = dataRequest(FakeRequest())

        MockSubmitChangeDestinationConnector.submit(minimumSubmitChangeDestinationModel).returns(Future.successful(Right(submitChangeDestinationResponseEIS)))

        MockAuditingService
          .audit(SubmitChangeDestinationAudit(testErn, testReceiptDateTime, minimumSubmitChangeDestinationModel, Right(submitChangeDestinationResponseEIS)))
          .once()

        testService.submit(minimumSubmitChangeDestinationModel)(request, hc).futureValue mustBe Right(submitChangeDestinationResponseEIS)
      }
    }

    "should return a Left" - {

      "when Connector returns failure from downstream" in {

        val request = dataRequest(FakeRequest())

        MockSubmitChangeDestinationConnector.submit(minimumSubmitChangeDestinationModel).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        MockAuditingService
          .audit(SubmitChangeDestinationAudit(testErn, testReceiptDateTime, minimumSubmitChangeDestinationModel, Left(UnexpectedDownstreamResponseError)))
          .once()

        testService.submit(minimumSubmitChangeDestinationModel)(request, hc).futureValue mustBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }
}
