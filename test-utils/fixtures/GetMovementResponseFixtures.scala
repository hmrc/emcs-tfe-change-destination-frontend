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
import play.api.libs.json.{JsObject, Json}

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
    competentAuthorityDispatchOfficeReferenceNumber = Some("GB123456"),
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

  lazy val maxGetMovementJson: JsObject = Json.obj(
    "arc"                                  -> "ExciseMovementArc",
    "sequenceNumber"                       -> 1,
    "destinationType"                      -> "1",
    "memberStateCode"                      -> "CCTMemberStateCode",
    "serialNumberOfCertificateOfExemption" -> "CCTSerialNumber",
    "consignorTrader" -> Json.obj(
      "traderExciseNumber" -> "ConsignorTraderExciseNumber",
      "traderName"         -> "ConsignorTraderName",
      "address" -> Json.obj(
        "streetNumber" -> "ConsignorTraderStreetNumber",
        "street"       -> "ConsignorTraderStreetName",
        "postcode"     -> "ConsignorTraderPostcode",
        "city"         -> "ConsignorTraderCity"
      )
    ),
    "consigneeTrader" -> Json.obj(
      "traderExciseNumber" -> "ConsigneeTraderId",
      "traderName"         -> "ConsigneeTraderName",
      "address" -> Json.obj(
        "streetNumber" -> "ConsigneeTraderStreetNumber",
        "street"       -> "ConsigneeTraderStreetName",
        "postcode"     -> "ConsigneeTraderPostcode",
        "city"         -> "ConsigneeTraderCity"
      ),
      "eoriNumber" -> "ConsigneeTraderEori"
    ),
    "deliveryPlaceTrader" -> Json.obj(
      "traderExciseNumber" -> "DeliveryPlaceTraderId",
      "traderName"         -> "DeliveryPlaceTraderName",
      "address" -> Json.obj(
        "streetNumber" -> "DeliveryPlaceTraderStreetNumber",
        "street"       -> "DeliveryPlaceTraderStreetName",
        "postcode"     -> "DeliveryPlaceTraderPostcode",
        "city"         -> "DeliveryPlaceTraderCity"
      )
    ),
    "placeOfDispatchTrader" -> Json.obj(
      "traderExciseNumber" -> "PlaceOfDispatchTraderReferenceOfTaxWarehouse",
      "traderName"         -> "PlaceOfDispatchTraderName",
      "address" -> Json.obj(
        "streetNumber" -> "PlaceOfDispatchTraderStreetNumber",
        "street"       -> "PlaceOfDispatchTraderStreetName",
        "postcode"     -> "PlaceOfDispatchTraderPostcode",
        "city"         -> "PlaceOfDispatchTraderCity"
      )
    ),
    "transportArrangerTrader" -> Json.obj(
      "traderName" -> "TransportArrangerTraderName",
      "address" -> Json.obj(
        "streetNumber" -> "TransportArrangerTraderStreetNumber",
        "street"       -> "TransportArrangerTraderStreetName",
        "postcode"     -> "TransportArrangerTraderPostcode",
        "city"         -> "TransportArrangerTraderCity"
      ),
      "vatNumber" -> "TransportArrangerTraderVatNumber"
    ),
    "firstTransporterTrader" -> Json.obj(
      "traderName" -> "FirstTransporterTraderName",
      "address" -> Json.obj(
        "streetNumber" -> "FirstTransporterTraderStreetNumber",
        "street"       -> "FirstTransporterTraderStreetName",
        "postcode"     -> "FirstTransporterTraderPostcode",
        "city"         -> "FirstTransporterTraderCity"
      ),
      "vatNumber" -> "FirstTransporterTraderVatNumber"
    ),
    "dispatchImportOfficeReferenceNumber"             -> "DispatchImportOfficeErn",
    "deliveryPlaceCustomsOfficeReferenceNumber"       -> "DeliveryPlaceCustomsOfficeErn",
    "competentAuthorityDispatchOfficeReferenceNumber" -> "GB123456",
    "localReferenceNumber"                            -> "EadEsadLocalReferenceNumber",
    "eadStatus"                                       -> "Beans",
    "dateAndTimeOfValidationOfEadEsad"                -> "ExciseMovementDateTime",
    "dateOfDispatch"                                  -> "EadEsadDateOfDispatch",
    "journeyTime"                                     -> "10 hours",
    "documentCertificate" -> Json.arr(
      Json.obj(
        "documentType"        -> "DocumentCertificateDocumentType1",
        "documentReference"   -> "DocumentCertificateDocumentReference1",
        "documentDescription" -> "DocumentCertificateDocumentDescription1",
        "referenceOfDocument" -> "DocumentCertificateReferenceOfDocument1"
      ),
      Json.obj(
        "documentType"        -> "DocumentCertificateDocumentType2",
        "documentReference"   -> "DocumentCertificateDocumentReference2",
        "documentDescription" -> "DocumentCertificateDocumentDescription2",
        "referenceOfDocument" -> "DocumentCertificateReferenceOfDocument2"
      )
    ),
    "eadEsad" -> Json.obj(
      "localReferenceNumber" -> "EadEsadLocalReferenceNumber",
      "invoiceNumber"        -> "EadEsadInvoiceNumber",
      "invoiceDate"          -> "EadEsadInvoiceDate",
      "originTypeCode"       -> "3",
      "dateOfDispatch"       -> "EadEsadDateOfDispatch",
      "timeOfDispatch"       -> "EadEsadTimeOfDispatch",
      "upstreamArc"          -> "EadEsadUpstreamArc",
      "importSadNumber"      -> Json.arr("ImportSadNumber1", "ImportSadNumber2")
    ),
    "headerEadEsad" -> Json.obj(
      "sequenceNumber"                -> 1,
      "dateAndTimeOfUpdateValidation" -> "HeaderEadEsadDateTime",
      "destinationType"               -> "10",
      "journeyTime"                   -> "10 hours",
      "transportArrangement"          -> "2"
    ),
    "transportMode" -> Json.obj(
      "transportModeCode"        -> "TransportModeTransportModeCode",
      "complementaryInformation" -> "TransportModeComplementaryInformation"
    ),
    "movementGuarantee" -> Json.obj(
      "guarantorTypeCode" -> "123",
      "guarantorTrader" -> Json.arr(
        Json.obj(
          "traderExciseNumber" -> "GuarantorTraderErn1",
          "traderName"         -> "GuarantorTraderName1",
          "address" -> Json.obj(
            "streetNumber" -> "GuarantorTraderStreetNumber1",
            "street"       -> "GuarantorTraderStreetName1",
            "postcode"     -> "GuarantorTraderPostcode1",
            "city"         -> "GuarantorTraderCity1"
          ),
          "vatNumber" -> "GuarantorTraderVatNumber1"
        ),
        Json.obj(
          "traderExciseNumber" -> "GuarantorTraderErn2",
          "traderName"         -> "GuarantorTraderName2",
          "address" -> Json.obj(
            "streetNumber" -> "GuarantorTraderStreetNumber2",
            "street"       -> "GuarantorTraderStreetName2",
            "postcode"     -> "GuarantorTraderPostcode2",
            "city"         -> "GuarantorTraderCity2"
          ),
          "vatNumber" -> "GuarantorTraderVatNumber2"
        )
      )
    ),
    "items" -> Json.arr(
      Json.obj(
        "itemUniqueReference"   -> 1,
        "productCode"           -> "BodyEadEsadExciseProductCode1",
        "cnCode"                -> "BodyEadEsadCnCode1",
        "quantity"              -> 2,
        "grossMass"             -> 3,
        "netMass"               -> 4,
        "alcoholicStrength"     -> 5,
        "degreePlato"           -> 6,
        "fiscalMark"            -> "BodyEadEsadFiscalMark1",
        "fiscalMarkUsedFlag"    -> true,
        "designationOfOrigin"   -> "BodyEadEsadDesignationOfOrigin1",
        "sizeOfProducer"        -> "BodyEadEsadSizeOfProducer1",
        "density"               -> 7,
        "commercialDescription" -> "BodyEadEsadCommercialDescription1",
        "brandNameOfProduct"    -> "BodyEadEsadBrandNameOfProducts1",
        "maturationAge"         -> "BodyEadEsadMaturationPeriodOrAgeOfProducts1",
        "packaging" -> Json.arr(
          Json.obj(
            "typeOfPackage"            -> "PackageKindOfPackages11",
            "quantity"                 -> 1,
            "shippingMarks"            -> "PackageShippingMarks11",
            "identityOfCommercialSeal" -> "PackageCommercialSealIdentification11",
            "sealInformation"          -> "PackageSealInformation11"
          ),
          Json.obj(
            "typeOfPackage"            -> "PackageKindOfPackages12",
            "quantity"                 -> 2,
            "shippingMarks"            -> "PackageShippingMarks12",
            "identityOfCommercialSeal" -> "PackageCommercialSealIdentification12",
            "sealInformation"          -> "PackageSealInformation12"
          )
        ),
        "wineProduct" -> Json.obj(
          "wineProductCategory"  -> "1",
          "wineGrowingZoneCode"  -> "WineProductWineGrowingZoneCode1",
          "thirdCountryOfOrigin" -> "WineProductThirdCountryOfOrigin1",
          "otherInformation"     -> "WineProductOtherInformation1",
          "wineOperations"       -> Json.arr("WineOperationCode11", "WineOperationCode12")
        )
      ),
      Json.obj(
        "itemUniqueReference"   -> 2,
        "productCode"           -> "BodyEadEsadExciseProductCode2",
        "cnCode"                -> "BodyEadEsadCnCode2",
        "quantity"              -> 3,
        "grossMass"             -> 4,
        "netMass"               -> 5,
        "alcoholicStrength"     -> 6,
        "degreePlato"           -> 7,
        "fiscalMark"            -> "BodyEadEsadFiscalMark2",
        "fiscalMarkUsedFlag"    -> false,
        "designationOfOrigin"   -> "BodyEadEsadDesignationOfOrigin2",
        "sizeOfProducer"        -> "BodyEadEsadSizeOfProducer2",
        "density"               -> 8,
        "commercialDescription" -> "BodyEadEsadCommercialDescription2",
        "brandNameOfProduct"    -> "BodyEadEsadBrandNameOfProducts2",
        "maturationAge"         -> "BodyEadEsadMaturationPeriodOrAgeOfProducts2",
        "packaging" -> Json.arr(
          Json.obj(
            "typeOfPackage"            -> "PackageKindOfPackages21",
            "quantity"                 -> 3,
            "shippingMarks"            -> "PackageShippingMarks21",
            "identityOfCommercialSeal" -> "PackageCommercialSealIdentification21",
            "sealInformation"          -> "PackageSealInformation21"
          ),
          Json.obj(
            "typeOfPackage"            -> "PackageKindOfPackages22",
            "quantity"                 -> 4,
            "shippingMarks"            -> "PackageShippingMarks22",
            "identityOfCommercialSeal" -> "PackageCommercialSealIdentification22",
            "sealInformation"          -> "PackageSealInformation22"
          )
        ),
        "wineProduct" -> Json.obj(
          "wineProductCategory"  -> "2",
          "wineGrowingZoneCode"  -> "WineProductWineGrowingZoneCode2",
          "thirdCountryOfOrigin" -> "WineProductThirdCountryOfOrigin2",
          "otherInformation"     -> "WineProductOtherInformation2",
          "wineOperations"       -> Json.arr("WineOperationCode21", "WineOperationCode22")
        )
      )
    ),
    "numberOfItems" -> 2,
    "transportDetails" -> Json.arr(
      Json.obj(
        "transportUnitCode"            -> "TransportDetailsTransportUnitCode1",
        "identityOfTransportUnits"     -> "TransportDetailsIdentityOfTransportUnits1",
        "commercialSealIdentification" -> "TransportDetailsCommercialSealIdentification1",
        "complementaryInformation"     -> "TransportDetailsComplementaryInformation1",
        "sealInformation"              -> "TransportDetailsSealInformation1"
      ),
      Json.obj(
        "transportUnitCode"            -> "TransportDetailsTransportUnitCode2",
        "identityOfTransportUnits"     -> "TransportDetailsIdentityOfTransportUnits2",
        "commercialSealIdentification" -> "TransportDetailsCommercialSealIdentification2",
        "complementaryInformation"     -> "TransportDetailsComplementaryInformation2",
        "sealInformation"              -> "TransportDetailsSealInformation2"
      )
    )
  )
}
