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
import featureswitch.core.config.EnableNRS
import fixtures.{NRSBrokerFixtures, SubmitChangeDestinationFixtures}
import mocks.config.MockAppConfig
import mocks.connectors.MockSubmitChangeDestinationConnector
import mocks.services.{MockAuditingService, MockNRSBrokerService}
import models.audit.SubmitChangeDestinationAudit
import models.response.UnexpectedDownstreamResponseError
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import utils.TimeMachine

import java.time.{Instant, LocalDateTime}
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

class SubmitChangeDestinationServiceSpec
  extends SpecBase
    with MockSubmitChangeDestinationConnector
    with SubmitChangeDestinationFixtures
    with MockAuditingService
    with MockAppConfig
    with MockNRSBrokerService
    with NRSBrokerFixtures {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  val timeMachine: TimeMachine = new TimeMachine {
    override def now(): LocalDateTime = LocalDateTime.parse(testReceiptDateTime)
    override def instant(): Instant = Instant.now()
  }

  lazy val testService = new SubmitChangeDestinationService(mockSubmitChangeDestinationConnector, mockNRSBrokerService, mockAuditingService, timeMachine, mockAppConfig)

  class Fixture(isNRSEnabled: Boolean) {

    MockAppConfig.getFeatureSwitchValue(EnableNRS).returns(isNRSEnabled)

    if (isNRSEnabled) {
      MockNRSBrokerService.submitPayload(minimumSubmitChangeDestinationModel, testErn)
        .returns(Future.successful(Right(nrsBrokerResponseModel)))
    } else {
      MockNRSBrokerService.submitPayload(minimumSubmitChangeDestinationModel, testErn).never()
    }
  }

  ".submit(ern: String, submission: SubmitChangeDestinationModel)" - {

    Seq(true, false).foreach { nrsEnabled =>

      s"when NRS enabled is '$nrsEnabled'" - {

        "should return a Right" - {

          "when Connector returns success from downstream" in new Fixture(nrsEnabled) {

            val request = dataRequest(FakeRequest())

            MockSubmitChangeDestinationConnector.submit(minimumSubmitChangeDestinationModel).returns(Future.successful(Right(submitChangeDestinationResponseEIS)))

            MockAuditingService
              .audit(SubmitChangeDestinationAudit(testErn, testReceiptDateTime, minimumSubmitChangeDestinationModel, Right(submitChangeDestinationResponseEIS)))
              .once()

            testService.submit(minimumSubmitChangeDestinationModel, testErn)(request, hc).futureValue mustBe Right(submitChangeDestinationResponseEIS)
          }
        }

        "should return a Left" - {

          "when Connector returns failure from downstream" in new Fixture(nrsEnabled) {

            val request = dataRequest(FakeRequest())

            MockSubmitChangeDestinationConnector.submit(minimumSubmitChangeDestinationModel).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

            MockAuditingService
              .audit(SubmitChangeDestinationAudit(testErn, testReceiptDateTime, minimumSubmitChangeDestinationModel, Left(UnexpectedDownstreamResponseError)))
              .once()

            testService.submit(minimumSubmitChangeDestinationModel, testErn)(request, hc).futureValue mustBe Left(UnexpectedDownstreamResponseError)
          }
        }
      }
    }
  }
}
