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

package models.submitChangeDestination

import models.requests.DataRequest
import models.sections.journeyType.HowMovementTransported.Other
import models.sections.journeyType.JourneyTime
import models.sections.journeyType.JourneyTime.{Days, Hours}
import models.sections.transportArranger.TransportArranger
import pages.sections.journeyType._
import pages.sections.movement.{InvoiceDetailsPage, MovementReviewAnswersPage}
import pages.sections.transportArranger.{TransportArrangerPage, TransportArrangerReviewPage}
import play.api.libs.json.{Json, OFormat}
import utils.{Logging, ModelConstructorHelpers}

case class UpdateEadEsadModel(
                               administrativeReferenceCode: String,
                               journeyTime: Option[JourneyTime],
                               changedTransportArrangement: Option[TransportArranger],
                               sequenceNumber: Option[String],
                               invoiceDate: Option[String],
                               invoiceNumber: Option[String],
                               transportModeCode: Option[String],
                               complementaryInformation: Option[String]
                             )

object UpdateEadEsadModel extends ModelConstructorHelpers with Logging {

  implicit val fmt: OFormat[UpdateEadEsadModel] = Json.format

  private[submitChangeDestination] def journeyTimeValue(implicit request: DataRequest[_]): Option[JourneyTime] =
    (request.userAnswers.getFromUserAnswersOnly(JourneyTimeHoursPage), request.userAnswers.getFromUserAnswersOnly(JourneyTimeDaysPage)) match {
      case (Some(hours), _) => Some(Hours(hours.toString))
      case (_, Some(days)) => Some(Days(days.toString))
      case _ => None
    }

  def apply(implicit request: DataRequest[_]): UpdateEadEsadModel =
    UpdateEadEsadModel(
      administrativeReferenceCode = request.arc,
      journeyTime = whenSectionChanged(JourneyTypeReviewPage)(journeyTimeValue).flatten,
      changedTransportArrangement = whenSectionChanged(TransportArrangerReviewPage)(mandatoryPage(TransportArrangerPage)),
      sequenceNumber = None, // As per DDNEA rules, the sequence number should be added to the XML payload only after it has passed the RIM validation
      invoiceDate = whenSectionChanged(MovementReviewAnswersPage)(mandatoryPage(InvoiceDetailsPage).date.toString),
      invoiceNumber = whenSectionChanged(MovementReviewAnswersPage)(mandatoryPage(InvoiceDetailsPage).reference),
      transportModeCode = whenSectionChanged(JourneyTypeReviewPage)(mandatoryPage(HowMovementTransportedPage).toString),
      complementaryInformation = whenSectionChanged(JourneyTypeReviewPage){
        Option.when(request.userAnswers.get(HowMovementTransportedPage).contains(Other)) {
          mandatoryPage(GiveInformationOtherTransportPage)
        }
      }.flatten
    )
}
