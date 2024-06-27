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

import connectors.emcsTfe.SubmitChangeDestinationConnector
import models.audit.SubmitChangeDestinationAudit
import models.requests.DataRequest
import models.response.{ErrorResponse, SubmitChangeDestinationException, SubmitChangeDestinationResponse}
import models.submitChangeDestination.SubmitChangeDestinationModel
import uk.gov.hmrc.http.HeaderCarrier
import utils.{Logging, TimeMachine}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubmitChangeDestinationService @Inject()(
                                                connector: SubmitChangeDestinationConnector,
                                                auditingService: AuditingService,
                                                timeMachine: TimeMachine)(implicit ec: ExecutionContext) extends Logging {

  def submit(submitChangeDestinationModel: SubmitChangeDestinationModel)
            (implicit request: DataRequest[_], hc: HeaderCarrier): Future[SubmitChangeDestinationResponse] =

    connector.submit(submitChangeDestinationModel).map {
      case Right(success) =>
        writeAudit(submitChangeDestinationModel, Right(success))
        success
      case Left(value) =>
        writeAudit(submitChangeDestinationModel, Left(value))
        logger.warn(s"Received Left from SubmitChangeDestinationConnector: $value")
        throw SubmitChangeDestinationException(s"Failed to submit Change Destination to emcs-tfe for ern: '${request.ern}' & arc: '${request.arc}'")
    }

  private def writeAudit(
                          submissionRequest: SubmitChangeDestinationModel,
                          submissionResponse: Either[ErrorResponse, SubmitChangeDestinationResponse]
                        )(implicit hc: HeaderCarrier, dataRequest: DataRequest[_]): Unit =

    auditingService.audit(
      SubmitChangeDestinationAudit(
        ern = dataRequest.ern,
        submissionRequest = submissionRequest,
        submissionResponse = submissionResponse,
        receiptDate = timeMachine.now().toString
      )
    )
}
