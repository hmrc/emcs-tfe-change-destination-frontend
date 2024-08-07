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

package models.response.emcsTfe

import models.sections.info.movementScenario.DestinationType
import play.api.libs.json.{Reads, __}


case class GetMovementResponse(
                                arc: String,
                                sequenceNumber: Int,
                                destinationType: DestinationType,
                                memberStateCode: Option[String],
                                serialNumberOfCertificateOfExemption: Option[String],
                                consignorTrader: TraderModel,
                                consigneeTrader: Option[TraderModel],
                                deliveryPlaceTrader: Option[TraderModel],
                                placeOfDispatchTrader: Option[TraderModel],
                                transportArrangerTrader: Option[TraderModel],
                                firstTransporterTrader: Option[TraderModel],
                                dispatchImportOfficeReferenceNumber: Option[String],
                                deliveryPlaceCustomsOfficeReferenceNumber: Option[String],
                                competentAuthorityDispatchOfficeReferenceNumber: Option[String],
                                localReferenceNumber: String,
                                eadStatus: String,
                                dateAndTimeOfValidationOfEadEsad: String,
                                dateOfDispatch: String,
                                journeyTime: String,
                                documentCertificate: Option[Seq[DocumentCertificateModel]],
                                eadEsad: EadEsadModel,
                                headerEadEsad: HeaderEadEsadModel,
                                transportMode: TransportModeModel,
                                movementGuarantee: MovementGuaranteeModel,
                                items: Seq[MovementItem],
                                transportDetails: Seq[TransportDetailsModel]
                              ) {
}

object GetMovementResponse {
  implicit lazy val reads: Reads[GetMovementResponse] = for {
    arc <- (__ \ "arc").read[String]
    sequenceNumber <- (__ \ "sequenceNumber").read[Int]
    destinationType <- (__ \ "destinationType").read[DestinationType]
    memberStateCode <- (__ \ "memberStateCode").readNullable[String]
    serialNumberOfCertificateOfExemption <- (__ \ "serialNumberOfCertificateOfExemption").readNullable[String]
    consignorTrader <- (__ \ "consignorTrader").read[TraderModel]
    consigneeTrader <- (__ \ "consigneeTrader").readNullable[TraderModel]
    deliveryPlaceTrader <- (__ \ "deliveryPlaceTrader").readNullable[TraderModel]
    placeOfDispatchTrader <- (__ \ "placeOfDispatchTrader").readNullable[TraderModel]
    transportArrangerTrader <- (__ \ "transportArrangerTrader").readNullable[TraderModel]
    firstTransporterTrader <- (__ \ "firstTransporterTrader").readNullable[TraderModel]
    dispatchImportOfficeReferenceNumber <- (__ \ "dispatchImportOfficeReferenceNumber").readNullable[String]
    deliveryPlaceCustomsOfficeReferenceNumber <- (__ \ "deliveryPlaceCustomsOfficeReferenceNumber").readNullable[String]
    competentAuthorityDispatchOfficeReferenceNumber <- (__ \ "competentAuthorityDispatchOfficeReferenceNumber").readNullable[String]
    localReferenceNumber <- (__ \ "localReferenceNumber").read[String]
    eadStatus <- (__ \ "eadStatus").read[String]
    dateAndTimeOfValidationOfEadEsad <- (__ \ "dateAndTimeOfValidationOfEadEsad").read[String]
    dateOfDispatch <- (__ \ "dateOfDispatch").read[String]
    journeyTime <- (__ \ "journeyTime").read[String]
    documentCertificate <- (__ \ "documentCertificate").readNullable[Seq[DocumentCertificateModel]]
    eadEsad <- (__ \ "eadEsad").read[EadEsadModel]
    headerEadEsad <- (__ \ "headerEadEsad").read[HeaderEadEsadModel]
    transportMode <- (__ \ "transportMode").read[TransportModeModel]
    movementGuarantee <- (__ \ "movementGuarantee").read[MovementGuaranteeModel]
    items <- (__ \ "items").read[Seq[MovementItem]]
    transportDetails <- (__ \ "transportDetails").read[Seq[TransportDetailsModel]]
  } yield {
    GetMovementResponse(
      arc = arc,
      sequenceNumber = sequenceNumber,
      destinationType = destinationType,
      memberStateCode = memberStateCode,
      serialNumberOfCertificateOfExemption = serialNumberOfCertificateOfExemption,
      consignorTrader = consignorTrader,
      consigneeTrader = consigneeTrader,
      deliveryPlaceTrader = deliveryPlaceTrader,
      placeOfDispatchTrader = placeOfDispatchTrader,
      transportArrangerTrader = transportArrangerTrader,
      firstTransporterTrader = firstTransporterTrader,
      dispatchImportOfficeReferenceNumber = dispatchImportOfficeReferenceNumber,
      deliveryPlaceCustomsOfficeReferenceNumber = deliveryPlaceCustomsOfficeReferenceNumber,
      competentAuthorityDispatchOfficeReferenceNumber = competentAuthorityDispatchOfficeReferenceNumber,
      localReferenceNumber = localReferenceNumber,
      eadStatus = eadStatus,
      dateAndTimeOfValidationOfEadEsad = dateAndTimeOfValidationOfEadEsad,
      dateOfDispatch = dateOfDispatch,
      journeyTime = journeyTime,
      documentCertificate = documentCertificate,
      eadEsad = eadEsad,
      headerEadEsad = headerEadEsad,
      transportMode = transportMode,
      movementGuarantee = movementGuarantee,
      items = items,
      transportDetails = transportDetails
    )
  }
}
