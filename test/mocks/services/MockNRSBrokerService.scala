/*
 * Copyright 2024 HM Revenue & Customs
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

package mocks.services

import models.requests.DataRequest
import models.response.ErrorResponse
import models.response.nrsBroker.NRSBrokerInsertPayloadResponse
import models.submitChangeDestination.SubmitChangeDestinationModel
import org.scalamock.handlers.CallHandler5
import org.scalamock.scalatest.MockFactory
import services.nrs.NRSBrokerService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockNRSBrokerService extends MockFactory {

  val mockNRSBrokerService: NRSBrokerService = mock[NRSBrokerService]

  object MockNRSBrokerService {

    def submitPayload(submission: SubmitChangeDestinationModel, ern: String): CallHandler5[SubmitChangeDestinationModel, String, HeaderCarrier, ExecutionContext, DataRequest[_], Future[Either[ErrorResponse, NRSBrokerInsertPayloadResponse]]] =
      (mockNRSBrokerService.submitPayload(_: SubmitChangeDestinationModel, _: String)(_: HeaderCarrier, _: ExecutionContext, _: DataRequest[_]))
        .expects(submission, ern, *, *, *)
  }

}
