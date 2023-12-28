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

import models.response.SubmitChangeDestinationResponse
import models.sections.guarantor.GuarantorArranger
import models.sections.info.movementScenario.{DestinationType, MovementType, OriginType}
import models.sections.journeyType.HowMovementTransported
import models.sections.transportArranger.TransportArranger
import models.sections.transportUnit.TransportUnitType
import models.submitChangeDestination.{AddressModel, AttributesModel, ComplementConsigneeTraderModel, EadEsadDraftModel, HeaderEadEsadModel, MovementGuaranteeModel, OfficeModel, SubmissionMessageType, SubmitChangeDestinationModel, TraderModel, TransportDetailsModel, TransportModeModel}
import play.api.libs.json.{JsValue, Json}

trait SubmitChangeDestinationFixtures { _: BaseFixtures =>

  //TODO: None of these models are valid. They are taken from the SubmitCreateMovementModeel in CaM
  //      All of these models and tests data need refactoring once we play the story to actual construct
  //      the CoD submission to emcs-tfe

  val xircSubmitChangeDestinationModel: SubmitChangeDestinationModel = SubmitChangeDestinationModel(
    movementType = MovementType.ImportEu,
    attributes = AttributesModel(SubmissionMessageType.Standard),
    consigneeTrader = Some(TraderModel(
      traderExciseNumber = Some("consignee ern"),
      traderName = Some("consignee name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignee street"))),
      vatNumber = None,
      eoriNumber = Some("consignee eori")
    )),
    consignorTrader = TraderModel(
      traderExciseNumber = Some("XIRC123"),
      traderName = Some(testMinTraderKnownFacts.traderName),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignor street"))),
      vatNumber = None,
      eoriNumber = None
    ),
    complementConsigneeTrader = Some(ComplementConsigneeTraderModel("state", Some("number"))),
    deliveryPlaceTrader = Some(TraderModel(
      traderExciseNumber = None,
      traderName = Some("destination name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "destination street"))),
      vatNumber = None,
      eoriNumber = None
    )),
    deliveryPlaceCustomsOffice = Some(OfficeModel("delivery place customs office")),
    competentAuthorityDispatchOffice = OfficeModel(s"XI$dispatchOfficeSuffix"),
    transportArrangerTrader = Some(TraderModel(
      traderExciseNumber = None,
      traderName = Some("arranger name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "arranger street"))),
      vatNumber = Some("arranger vat"),
      eoriNumber = None
    )),
    firstTransporterTrader = Some(TraderModel(
      traderExciseNumber = None,
      traderName = Some("first name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "first street"))),
      vatNumber = Some("first vat"),
      eoriNumber = None
    )),
    headerEadEsad = HeaderEadEsadModel(
      destinationType = DestinationType.DirectDelivery,
      journeyTime = "2 hours",
      transportArrangement = TransportArranger.GoodsOwner
    ),
    transportMode = TransportModeModel(
      transportModeCode = HowMovementTransported.AirTransport.toString,
      complementaryInformation = Some("info")
    ),
    movementGuarantee = MovementGuaranteeModel(
      guarantorTypeCode = GuarantorArranger.GoodsOwner,
      guarantorTrader = Some(Seq(TraderModel(
        traderExciseNumber = None,
        traderName = Some("guarantor name"),
        address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "guarantor street"))),
        vatNumber = Some("guarantor vat"),
        eoriNumber = None
      )))
    ),
    eadEsadDraft = EadEsadDraftModel(
      originTypeCode = OriginType.Imports,
      dateOfDispatch = "2020-10-31",
      timeOfDispatch = Some("23:59:59")
    ),
    transportDetails = Seq(
      TransportDetailsModel(
        transportUnitCode = TransportUnitType.FixedTransport.toString,
        identityOfTransportUnits = Some("identity"),
        commercialSealIdentification = Some("seal type"),
        complementaryInformation = Some("more info"),
        sealInformation = Some("seal info")
      )
    )
  )

  val xiwkSubmitChangeDestinationModel: SubmitChangeDestinationModel = SubmitChangeDestinationModel(
    movementType = MovementType.UkToEu,
    attributes = AttributesModel(SubmissionMessageType.Standard),
    consigneeTrader = Some(TraderModel(
      traderExciseNumber = Some("consignee ern"),
      traderName = Some("consignee name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignee street"))),
      vatNumber = None,
      eoriNumber = Some("consignee eori")
    )),
    consignorTrader = TraderModel(
      traderExciseNumber = Some("XIWK123"),
      traderName = Some(testMinTraderKnownFacts.traderName),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignor street"))),
      vatNumber = None,
      eoriNumber = None
    ),
    complementConsigneeTrader = Some(ComplementConsigneeTraderModel("state", Some("number"))),
    deliveryPlaceTrader = Some(TraderModel(
      traderExciseNumber = None,
      traderName = Some("destination name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "destination street"))),
      vatNumber = None,
      eoriNumber = None
    )),
    deliveryPlaceCustomsOffice = Some(OfficeModel("delivery place customs office")),
    competentAuthorityDispatchOffice = OfficeModel(s"XI$dispatchOfficeSuffix"),
    transportArrangerTrader = Some(TraderModel(
      traderExciseNumber = None,
      traderName = Some("arranger name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "arranger street"))),
      vatNumber = Some("arranger vat"),
      eoriNumber = None
    )),
    firstTransporterTrader = Some(TraderModel(
      traderExciseNumber = None,
      traderName = Some("first name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "first street"))),
      vatNumber = Some("first vat"),
      eoriNumber = None
    )),
    headerEadEsad = HeaderEadEsadModel(
      destinationType = DestinationType.DirectDelivery,
      journeyTime = "2 hours",
      transportArrangement = TransportArranger.GoodsOwner
    ),
    transportMode = TransportModeModel(
      transportModeCode = HowMovementTransported.AirTransport.toString,
      complementaryInformation = Some("info")
    ),
    movementGuarantee = MovementGuaranteeModel(
      guarantorTypeCode = GuarantorArranger.GoodsOwner,
      guarantorTrader = Some(Seq(TraderModel(
        traderExciseNumber = None,
        traderName = Some("guarantor name"),
        address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "guarantor street"))),
        vatNumber = Some("guarantor vat"),
        eoriNumber = None
      )))
    ),
    eadEsadDraft = EadEsadDraftModel(
      originTypeCode = OriginType.TaxWarehouse,
      dateOfDispatch = "2020-10-31",
      timeOfDispatch = Some("23:59:59")
    ),
    transportDetails = Seq(
      TransportDetailsModel(
        transportUnitCode = TransportUnitType.FixedTransport.toString,
        identityOfTransportUnits = Some("identity"),
        commercialSealIdentification = Some("seal type"),
        complementaryInformation = Some("more info"),
        sealInformation = Some("seal info")
      )
    )
  )

  val gbrcSubmitChangeDestinationModel: SubmitChangeDestinationModel = SubmitChangeDestinationModel(
    movementType = MovementType.ImportEu,
    attributes = AttributesModel(SubmissionMessageType.Standard),
    consigneeTrader = Some(TraderModel(
      traderExciseNumber = Some("consignee ern"),
      traderName = Some("consignee name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignee street"))),
      vatNumber = None,
      eoriNumber = Some("consignee eori")
    )),
    consignorTrader = TraderModel(
      traderExciseNumber = Some("GBRC123"),
      traderName = Some(testMinTraderKnownFacts.traderName),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignor street"))),
      vatNumber = None,
      eoriNumber = None
    ),
    complementConsigneeTrader = Some(ComplementConsigneeTraderModel("state", Some("number"))),
    deliveryPlaceTrader = Some(TraderModel(
      traderExciseNumber = None,
      traderName = Some("destination name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "destination street"))),
      vatNumber = None,
      eoriNumber = None
    )),
    deliveryPlaceCustomsOffice = Some(OfficeModel("delivery place customs office")),
    competentAuthorityDispatchOffice = OfficeModel(s"GB$dispatchOfficeSuffix"),
    transportArrangerTrader = Some(TraderModel(
      traderExciseNumber = None,
      traderName = Some("arranger name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "arranger street"))),
      vatNumber = Some("arranger vat"),
      eoriNumber = None
    )),
    firstTransporterTrader = Some(TraderModel(
      traderExciseNumber = None,
      traderName = Some("first name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "first street"))),
      vatNumber = Some("first vat"),
      eoriNumber = None
    )),
    headerEadEsad = HeaderEadEsadModel(
      destinationType = DestinationType.DirectDelivery,
      journeyTime = "2 hours",
      transportArrangement = TransportArranger.GoodsOwner
    ),
    transportMode = TransportModeModel(
      transportModeCode = HowMovementTransported.AirTransport.toString,
      complementaryInformation = Some("info")
    ),
    movementGuarantee = MovementGuaranteeModel(
      guarantorTypeCode = GuarantorArranger.GoodsOwner,
      guarantorTrader = Some(Seq(TraderModel(
        traderExciseNumber = None,
        traderName = Some("guarantor name"),
        address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "guarantor street"))),
        vatNumber = Some("guarantor vat"),
        eoriNumber = None
      )))
    ),
    eadEsadDraft = EadEsadDraftModel(
      originTypeCode = OriginType.Imports,
      dateOfDispatch = "2020-10-31",
      timeOfDispatch = Some("23:59:59")
    ),
    transportDetails = Seq(
      TransportDetailsModel(
        transportUnitCode = TransportUnitType.FixedTransport.toString,
        identityOfTransportUnits = Some("identity"),
        commercialSealIdentification = Some("seal type"),
        complementaryInformation = Some("more info"),
        sealInformation = Some("seal info")
      )
    )
  )

  val gbwkSubmitChangeDestinationModel: SubmitChangeDestinationModel = SubmitChangeDestinationModel(
    movementType = MovementType.UkToEu,
    attributes = AttributesModel(SubmissionMessageType.Standard),
    consigneeTrader = Some(TraderModel(
      traderExciseNumber = Some("consignee ern"),
      traderName = Some("consignee name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignee street"))),
      vatNumber = None,
      eoriNumber = Some("consignee eori")
    )),
    consignorTrader = TraderModel(
      traderExciseNumber = Some("GBWK123"),
      traderName = Some(testMinTraderKnownFacts.traderName),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignor street"))),
      vatNumber = None,
      eoriNumber = None
    ),
    complementConsigneeTrader = Some(ComplementConsigneeTraderModel("state", Some("number"))),
    deliveryPlaceTrader = Some(TraderModel(
      traderExciseNumber = None,
      traderName = Some("destination name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "destination street"))),
      vatNumber = None,
      eoriNumber = None
    )),
    deliveryPlaceCustomsOffice = Some(OfficeModel("delivery place customs office")),
    competentAuthorityDispatchOffice = OfficeModel(s"GB$dispatchOfficeSuffix"),
    transportArrangerTrader = Some(TraderModel(
      traderExciseNumber = None,
      traderName = Some("arranger name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "arranger street"))),
      vatNumber = Some("arranger vat"),
      eoriNumber = None
    )),
    firstTransporterTrader = Some(TraderModel(
      traderExciseNumber = None,
      traderName = Some("first name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "first street"))),
      vatNumber = Some("first vat"),
      eoriNumber = None
    )),
    headerEadEsad = HeaderEadEsadModel(
      destinationType = DestinationType.DirectDelivery,
      journeyTime = "2 hours",
      transportArrangement = TransportArranger.GoodsOwner
    ),
    transportMode = TransportModeModel(
      transportModeCode = HowMovementTransported.AirTransport.toString,
      complementaryInformation = Some("info")
    ),
    movementGuarantee = MovementGuaranteeModel(
      guarantorTypeCode = GuarantorArranger.GoodsOwner,
      guarantorTrader = Some(Seq(TraderModel(
        traderExciseNumber = None,
        traderName = Some("guarantor name"),
        address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "guarantor street"))),
        vatNumber = Some("guarantor vat"),
        eoriNumber = None
      )))
    ),
    eadEsadDraft = EadEsadDraftModel(
      originTypeCode = OriginType.TaxWarehouse,
      dateOfDispatch = "2020-10-31",
      timeOfDispatch = Some("23:59:59")
    ),
    transportDetails = Seq(
      TransportDetailsModel(
        transportUnitCode = TransportUnitType.FixedTransport.toString,
        identityOfTransportUnits = Some("identity"),
        commercialSealIdentification = Some("seal type"),
        complementaryInformation = Some("more info"),
        sealInformation = Some("seal info")
      )
    )
  )

  val successResponseChRISJson: JsValue = Json.obj("receipt" -> testConfirmationReference, "receiptDate" -> "2023-06-07T10:11:12.000")
  val successResponseEISJson: JsValue = Json.parse(
    s"""{
       | "status": "OK",
       | "message": "$testConfirmationReference",
       | "emcsCorrelationId": "3e8dae97-b586-4cef-8511-68ac12da9028"
       |}""".stripMargin)
  val submitChangeDestinationResponseEIS: SubmitChangeDestinationResponse =
    SubmitChangeDestinationResponse(receipt = testConfirmationReference, downstreamService = "EIS")
  val submitChangeDestinationResponseChRIS: SubmitChangeDestinationResponse =
    SubmitChangeDestinationResponse(receipt = testConfirmationReference, downstreamService = "ChRIS")

}
