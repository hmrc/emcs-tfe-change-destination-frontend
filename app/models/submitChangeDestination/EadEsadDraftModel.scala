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

package models.submitChangeDestination

import models.requests.DataRequest
import models.sections.info.movementScenario.OriginType
import pages.sections.info._
import play.api.libs.json.{Json, OFormat}
import utils.ModelConstructorHelpers

import java.time.format.DateTimeFormatter

case class EadEsadDraftModel(
                              localReferenceNumber: String,
                              invoiceNumber: String,
                              invoiceDate: Option[String],
                              originTypeCode: OriginType,
                              dateOfDispatch: String,
                              timeOfDispatch: Option[String],
                              importSad: Option[Seq[ImportSadModel]]
                            )

object EadEsadDraftModel extends ModelConstructorHelpers {

  // XSD requires seconds so need to define a formatter which always has seconds - default formatter can potentially just be HH:mm
  private lazy val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

  def apply(implicit request: DataRequest[_]): EadEsadDraftModel = EadEsadDraftModel(
    localReferenceNumber = mandatoryPage(LocalReferenceNumberPage()),
    invoiceNumber = mandatoryPage(InvoiceDetailsPage()).reference,
    invoiceDate = Some(mandatoryPage(InvoiceDetailsPage()).date.toString),
    originTypeCode = mandatoryPage(DestinationTypePage).originType,
    dateOfDispatch = mandatoryPage(DispatchDetailsPage()).date.toString,
    timeOfDispatch = Some(mandatoryPage(DispatchDetailsPage()).time.format(timeFormatter)),
    importSad = if(request.isWarehouseKeeper) None else Some(ImportSadModel.apply)
  )

  implicit val fmt: OFormat[EadEsadDraftModel] = Json.format
}
