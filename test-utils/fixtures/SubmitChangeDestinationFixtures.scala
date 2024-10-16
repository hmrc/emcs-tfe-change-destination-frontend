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

import models.{UserAnswers, VatNumberModel}
import models.response.SubmitChangeDestinationResponse
import models.sections.ReviewAnswer.ChangeAnswers
import models.sections.consignee.ConsigneeExportInformation.VatNumber
import models.sections.guarantor.GuarantorArranger
import models.sections.info.ChangeType.ChangeConsignee
import models.sections.info.movementScenario.{DestinationType, MovementScenario}
import models.sections.journeyType.HowMovementTransported
import models.sections.journeyType.JourneyTime.Hours
import models.sections.transportArranger.TransportArranger
import models.sections.transportUnit.{TransportSealTypeModel, TransportUnitType}
import models.submitChangeDestination._
import pages.sections.consignee._
import pages.sections.exportInformation.ExportCustomsOfficePage
import pages.sections.firstTransporter.{FirstTransporterAddressPage, FirstTransporterNamePage, FirstTransporterReviewPage, FirstTransporterVatPage}
import pages.sections.guarantor._
import pages.sections.info.{ChangeDestinationTypePage, ChangeTypePage, DestinationTypePage}
import pages.sections.journeyType.{GiveInformationOtherTransportPage, HowMovementTransportedPage, JourneyTimeHoursPage, JourneyTypeReviewPage}
import pages.sections.movement.{InvoiceDetailsPage, MovementReviewAnswersPage}
import pages.sections.transportArranger._
import pages.sections.transportUnit._
import play.api.libs.json.{JsValue, Json}

trait SubmitChangeDestinationFixtures extends GetMovementResponseFixtures { _: BaseFixtures =>

  val existingFirstTransporter = TraderModel(
    traderExciseNumber = None,
    traderName = maxGetMovementResponse.firstTransporterTrader.flatMap(_.traderName),
    address = maxGetMovementResponse.firstTransporterTrader.flatMap(_.address.map { address =>
      AddressModel(
        address.streetNumber,
        address.street,
        address.postcode,
        address.city
      )
    }),
    vatNumber = maxGetMovementResponse.firstTransporterTrader.flatMap(_.vatNumber),
    eoriNumber = None
  )

  val newFirstTransporter = TraderModel(
    traderExciseNumber = None,
    traderName = Some("first name"),
    address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "first street"))),
    vatNumber = Some("first vat"),
    eoriNumber = None
  )

  val newTransportTrader = TraderModel(
    traderExciseNumber = None,
    traderName = Some("arranger name"),
    address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "arranger street"))),
    vatNumber = Some("arranger vat"),
    eoriNumber = None
  )

  val newConsigneeTrader = TraderModel(
    traderExciseNumber = Some("consignee ern"),
    traderName = Some("consignee name"),
    address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignee street"))),
    vatNumber = None,
    eoriNumber = Some("consignee eori")
  )

  val movementGurarntee = MovementGuaranteeModel(
    guarantorTypeCode = GuarantorArranger.GoodsOwner,
    guarantorTrader = Some(Seq(TraderModel(
      traderExciseNumber = None,
      traderName = Some("guarantor name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "guarantor street"))),
      vatNumber = Some("guarantor vat"),
      eoriNumber = None
    )))
  )

  val maxDestinationChangedExport = DestinationChangedModel(
    destinationTypeCode = DestinationType.Export,
    newConsigneeTrader = Some(newConsigneeTrader),
    deliveryPlaceTrader = None,
    deliveryPlaceCustomsOffice = Some(DeliveryPlaceCustomsOfficeModel("exportOffice")),
    movementGuarantee = Some(movementGurarntee)
  )

  val minDestinationChanged = DestinationChangedModel(
    destinationTypeCode = maxGetMovementResponse.destinationType,
    newConsigneeTrader = None,
    deliveryPlaceTrader = Some(TraderModel(
      traderExciseNumber = Some(testGreatBritainErn),
      traderName = Some("destination name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "destination street"))),
      vatNumber = None,
      eoriNumber = None
    )),
    deliveryPlaceCustomsOffice = None,
    movementGuarantee = None
  )

  val maxUpdateEadEsad = UpdateEadEsadModel(
    administrativeReferenceCode = maxGetMovementResponse.arc,
    journeyTime = Some(Hours("2")),
    changedTransportArrangement = Some(TransportArranger.GoodsOwner),
    sequenceNumber = None,
    invoiceDate = invoiceDetailsModel.date,
    invoiceNumber = Some(invoiceDetailsModel.reference),
    transportModeCode = Some(HowMovementTransported.Other),
    complementaryInformation = Some("info")
  )

  val minUpdateEadEsad = UpdateEadEsadModel(
    administrativeReferenceCode = maxGetMovementResponse.arc,
    journeyTime = None,
    changedTransportArrangement = None,
    sequenceNumber = None,
    invoiceDate = None,
    invoiceNumber = None,
    transportModeCode = None,
    complementaryInformation = None
  )

  val transportDetails = TransportDetailsModel(
      transportUnitCode = TransportUnitType.Vehicle,
      identityOfTransportUnits = Some("identity"),
      commercialSealIdentification = Some("seal type"),
      complementaryInformation = Some("more info"),
      sealInformation = Some("seal info")
    )

  val maxSubmitChangeDestination: SubmitChangeDestinationModel =
    SubmitChangeDestinationModel(
      newTransportArrangerTrader = Some(newTransportTrader),
      updateEadEsad = maxUpdateEadEsad,
      destinationChanged = maxDestinationChangedExport,
      newTransporterTrader = Some(newFirstTransporter),
      transportDetails = Some(Seq(transportDetails))
    )

  val minimumSubmitChangeDestinationModel: SubmitChangeDestinationModel =
    SubmitChangeDestinationModel(
      newTransportArrangerTrader = None,
      updateEadEsad = minUpdateEadEsad,
      destinationChanged = minDestinationChanged,
      newTransporterTrader = Some(existingFirstTransporter),
      transportDetails = None
    )

  val baseFullUserAnswers: UserAnswers = emptyUserAnswers
    // Info Section
    .set(ChangeTypePage, ChangeConsignee)
    .set(ChangeDestinationTypePage, true)
    .set(DestinationTypePage, MovementScenario.ExportWithCustomsDeclarationLodgedInTheUk)
    // Sections Reviews
    .set(MovementReviewAnswersPage, ChangeAnswers)
    .set(JourneyTypeReviewPage, ChangeAnswers)
    .set(FirstTransporterReviewPage, ChangeAnswers)
    .set(TransportUnitsReviewPage, ChangeAnswers)
    .set(TransportArrangerReviewPage, ChangeAnswers)
    .set(GuarantorReviewPage, ChangeAnswers)
    // Movement
    .set(InvoiceDetailsPage, invoiceDetailsModel)
    // consignee
    .set(ConsigneeBusinessNamePage, "consignee name")
    .set(ConsigneeExcisePage, "consignee ern")
    .set(ConsigneeExportInformationPage, Set(VatNumber))
    .set(ConsigneeExportVatPage, "vat no")
    .set(ConsigneeExportEoriPage, "consignee eori")
    .set(ConsigneeAddressPage, testUserAddress.copy(street = "consignee street"))
    // deliveryPlaceCustomsOffice
    .set(ExportCustomsOfficePage, "exportOffice")
    // transportArrangerTrader
    .set(TransportArrangerPage, TransportArranger.GoodsOwner)
    .set(TransportArrangerNamePage, "arranger name")
    .set(TransportArrangerAddressPage, testUserAddress.copy(street = "arranger street"))
    .set(TransportArrangerVatPage, VatNumberModel(hasVatNumber = true, Some("arranger vat")))
    // firstTransporterTrader
    .set(FirstTransporterNamePage, "first name")
    .set(FirstTransporterAddressPage, testUserAddress.copy(street = "first street"))
    .set(FirstTransporterVatPage, VatNumberModel(hasVatNumber = true, Some("first vat")))
    // headerEadEsad
    .set(JourneyTimeHoursPage, 2)
    // transportMode
    .set(HowMovementTransportedPage, HowMovementTransported.Other)
    .set(GiveInformationOtherTransportPage, "info")
    // movementGuarantee
    .set(GuarantorNamePage, "guarantor name")
    .set(GuarantorAddressPage, testUserAddress.copy(street = "guarantor street"))
    .set(GuarantorVatPage, "guarantor vat")
    .set(GuarantorArrangerPage, GuarantorArranger.GoodsOwner)
    .set(GuarantorNamePage, "guarantor name")
    .set(GuarantorAddressPage, testUserAddress.copy(street = "guarantor street"))
    .set(GuarantorVatPage, "guarantor vat")
    // transportDetails
    .set(TransportUnitTypePage(testIndex1), TransportUnitType.Vehicle)
    .set(TransportUnitIdentityPage(testIndex1), "identity")
    .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("seal type", Some("seal info")))
    .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("more info"))

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
