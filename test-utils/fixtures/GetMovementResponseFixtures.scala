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

package fixtures

import models.response.emcsTfe._
import models.sections.info.movementScenario.{DestinationType, OriginType}
import models.sections.transportArranger.TransportArranger

trait GetMovementResponseFixtures extends BaseFixtures {

  lazy val maxGetMovementResponse: GetMovementResponse = GetMovementResponse(
    arc = "ExciseMovementArc",
    sequenceNumber = 1,
    destinationType = DestinationType.TaxWarehouse,
    memberStateCode = Some("CCTMemberStateCode"),
    serialNumberOfCertificateOfExemption = Some("CCTSerialNumber"),
    consignorTrader = TraderModel(
      traderExciseNumber = Some("ConsignorTraderExciseNumber"),
      traderName = Some("ConsignorTraderName"),
      address = Some(
        AddressModel(
          streetNumber = Some("ConsignorTraderStreetNumber"),
          street = Some("ConsignorTraderStreetName"),
          postcode = Some("ConsignorTraderPostcode"),
          city = Some("ConsignorTraderCity")
        )),
      vatNumber = None,
      eoriNumber = None
    ),
    consigneeTrader = Some(
      TraderModel(
        traderExciseNumber = Some("ConsigneeTraderId"),
        traderName = Some("ConsigneeTraderName"),
        address = Some(AddressModel(
          streetNumber = Some("ConsigneeTraderStreetNumber"),
          street = Some("ConsigneeTraderStreetName"),
          postcode = Some("ConsigneeTraderPostcode"),
          city = Some("ConsigneeTraderCity")
        )),
        vatNumber = None,
        eoriNumber = Some("ConsigneeTraderEori")
      )),
    deliveryPlaceTrader = Some(
      TraderModel(
        traderExciseNumber = Some("DeliveryPlaceTraderId"),
        traderName = Some("DeliveryPlaceTraderName"),
        address = Some(AddressModel(
          streetNumber = Some("DeliveryPlaceTraderStreetNumber"),
          street = Some("DeliveryPlaceTraderStreetName"),
          postcode = Some("DeliveryPlaceTraderPostcode"),
          city = Some("DeliveryPlaceTraderCity")
        )),
        vatNumber = None,
        eoriNumber = None
      )),
    placeOfDispatchTrader = Some(
      TraderModel(
        traderExciseNumber = Some("PlaceOfDispatchTraderReferenceOfTaxWarehouse"),
        traderName = Some("PlaceOfDispatchTraderName"),
        address = Some(AddressModel(
          streetNumber = Some("PlaceOfDispatchTraderStreetNumber"),
          street = Some("PlaceOfDispatchTraderStreetName"),
          postcode = Some("PlaceOfDispatchTraderPostcode"),
          city = Some("PlaceOfDispatchTraderCity")
        )),
        vatNumber = None,
        eoriNumber = None
      )),
    transportArrangerTrader = Some(
      TraderModel(
        traderExciseNumber = None,
        traderName = Some("TransportArrangerTraderName"),
        address = Some(AddressModel(
          streetNumber = Some("TransportArrangerTraderStreetNumber"),
          street = Some("TransportArrangerTraderStreetName"),
          postcode = Some("TransportArrangerTraderPostcode"),
          city = Some("TransportArrangerTraderCity")
        )),
        vatNumber = Some("TransportArrangerTraderVatNumber"),
        eoriNumber = None
      )),
    firstTransporterTrader = Some(
      TraderModel(
        traderExciseNumber = None,
        traderName = Some("FirstTransporterTraderName"),
        address = Some(AddressModel(
          streetNumber = Some("FirstTransporterTraderStreetNumber"),
          street = Some("FirstTransporterTraderStreetName"),
          postcode = Some("FirstTransporterTraderPostcode"),
          city = Some("FirstTransporterTraderCity")
        )),
        vatNumber = Some("FirstTransporterTraderVatNumber"),
        eoriNumber = None
      )),
    dispatchImportOfficeReferenceNumber = Some("DispatchImportOfficeErn"),
    deliveryPlaceCustomsOfficeReferenceNumber = Some("DeliveryPlaceCustomsOfficeErn"),
    competentAuthorityDispatchOfficeReferenceNumber = Some("CompetentAuthorityDispatchOfficeErn"),
    localReferenceNumber = "EadEsadLocalReferenceNumber",
    eadStatus = "Beans",
    dateAndTimeOfValidationOfEadEsad = "ExciseMovementDateTime",
    dateOfDispatch = "EadEsadDateOfDispatch",
    journeyTime = "10 hours",
    documentCertificate = Some(
      Seq(
        DocumentCertificateModel(
          documentType = Some("DocumentCertificateDocumentType1"),
          documentReference = Some("DocumentCertificateDocumentReference1"),
          documentDescription = Some("DocumentCertificateDocumentDescription1"),
          referenceOfDocument = Some("DocumentCertificateReferenceOfDocument1")
        ),
        DocumentCertificateModel(
          documentType = Some("DocumentCertificateDocumentType2"),
          documentReference = Some("DocumentCertificateDocumentReference2"),
          documentDescription = Some("DocumentCertificateDocumentDescription2"),
          referenceOfDocument = Some("DocumentCertificateReferenceOfDocument2")
        )
      )
    ),
    eadEsad = EadEsadModel(
      localReferenceNumber = "EadEsadLocalReferenceNumber",
      invoiceNumber = "EadEsadInvoiceNumber",
      invoiceDate = Some("EadEsadInvoiceDate"),
      originTypeCode = OriginType.DutyPaid,
      dateOfDispatch = "EadEsadDateOfDispatch",
      timeOfDispatch = Some("EadEsadTimeOfDispatch"),
      upstreamArc = Some("EadEsadUpstreamArc"),
      importSadNumber = Some(Seq("ImportSadNumber1", "ImportSadNumber2"))
    ),
    headerEadEsad = HeaderEadEsadModel(
      sequenceNumber = 1,
      dateAndTimeOfUpdateValidation = "HeaderEadEsadDateTime",
      destinationType = DestinationType.TemporaryCertifiedConsignee,
      journeyTime = "10 hours",
      transportArrangement = TransportArranger.Consignee
    ),
    transportMode = TransportModeModel(
      transportModeCode = "TransportModeTransportModeCode",
      complementaryInformation = Some("TransportModeComplementaryInformation")
    ),
    movementGuarantee = MovementGuaranteeModel(
      guarantorTypeCode = GuarantorType.ConsignorTransporterOwner,
      guarantorTrader = Some(
        Seq(
          TraderModel(
            traderExciseNumber = Some("GuarantorTraderErn1"),
            traderName = Some("GuarantorTraderName1"),
            address = Some(
              AddressModel(
                streetNumber = Some("GuarantorTraderStreetNumber1"),
                street = Some("GuarantorTraderStreetName1"),
                postcode = Some("GuarantorTraderPostcode1"),
                city = Some("GuarantorTraderCity1")
              )),
            vatNumber = Some("GuarantorTraderVatNumber1"),
            eoriNumber = None
          ),
          TraderModel(
            traderExciseNumber = Some("GuarantorTraderErn2"),
            traderName = Some("GuarantorTraderName2"),
            address = Some(
              AddressModel(
                streetNumber = Some("GuarantorTraderStreetNumber2"),
                street = Some("GuarantorTraderStreetName2"),
                postcode = Some("GuarantorTraderPostcode2"),
                city = Some("GuarantorTraderCity2")
              )),
            vatNumber = Some("GuarantorTraderVatNumber2"),
            eoriNumber = None
          )
        )
      )
    ),
    transportDetails = Seq(
      TransportDetailsModel(
        transportUnitCode = "TransportDetailsTransportUnitCode1",
        identityOfTransportUnits = Some("TransportDetailsIdentityOfTransportUnits1"),
        commercialSealIdentification = Some("TransportDetailsCommercialSealIdentification1"),
        complementaryInformation = Some("TransportDetailsComplementaryInformation1"),
        sealInformation = Some("TransportDetailsSealInformation1")
      ),
      TransportDetailsModel(
        transportUnitCode = "TransportDetailsTransportUnitCode2",
        identityOfTransportUnits = Some("TransportDetailsIdentityOfTransportUnits2"),
        commercialSealIdentification = Some("TransportDetailsCommercialSealIdentification2"),
        complementaryInformation = Some("TransportDetailsComplementaryInformation2"),
        sealInformation = Some("TransportDetailsSealInformation2")
      )
    )
  )
}
