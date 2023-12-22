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

import models.UnitOfMeasure.{Kilograms, Litres20}
import models.response.referenceData.{BulkPackagingType, CnCodeInformation, ItemPackaging, WineOperations}
import models.sections.consignee._
import models.sections.guarantor.GuarantorArranger
import models.sections.info._
import models.sections.info.movementScenario.{DestinationType, MovementScenario, MovementType, OriginType}
import models.sections.items.ItemBulkPackagingCode._
import models.sections.journeyType.HowMovementTransported
import models.sections.transportArranger._
import models.sections.transportUnit.{TransportSealTypeModel, TransportUnitType}
import models.submitChangeDestination._
import models.{ExciseProductCode, ExemptOrganisationDetailsModel, GoodsType, UserAnswers}
import pages.sections.consignee._
import pages.sections.consignor._
import pages.sections.destination._
import pages.sections.exportInformation._
import pages.sections.firstTransporter._
import pages.sections.guarantor._
import pages.sections.info._
import pages.sections.journeyType._
import pages.sections.transportArranger._
import pages.sections.transportUnit._
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

import java.time.{LocalDate, LocalTime}

trait ItemFixtures {
  _: BaseFixtures =>

  val beerExciseProductCode = ExciseProductCode(
    code = "B000",
    description = "Beer",
    category = "B",
    categoryDescription = "Beer"
  )

  val wineExciseProductCode = ExciseProductCode(
    code = "W200",
    description = "Still wine and still fermented beverages other than wine and beer",
    category = "W",
    categoryDescription = "Wine and fermented beverages other than wine and beer"
  )

  val beerExciseProductCodeJson = Json.obj(
    "code" -> "B000",
    "description" -> "Beer",
    "category" -> "B",
    "categoryDescription" -> "Beer"
  )

  val wineExciseProductCodeJson = Json.obj(
    "code" -> "W200",
    "description" -> "Still wine and still fermented beverages other than wine and beer",
    "category" -> "W",
    "categoryDescription" -> "Wine and fermented beverages other than wine and beer"
  )

  val bulkPackagingTypesJson = Json.obj(
    "VG" -> "Bulk, gas (at 1031 mbar and 15°C)",
    "VQ" -> "Bulk, liquefied gas (abn.temp/press)",
    "VL" -> "Bulk, liquid",
    "VY" -> "Bulk, solid, fine (powders)",
    "VR" -> "Bulk, solid, granular (grains)",
    "VO" -> "Bulk, solid, large (nodules)",
    "NE" -> "Unpacked or unpackaged"
  )

  val bulkPackagingTypes: Seq[BulkPackagingType] = Seq(
    BulkPackagingType(BulkGas, "Bulk, gas (at 1031 mbar and 15°C)"),
    BulkPackagingType(BulkLiquefiedGas, "Bulk, liquefied gas (abn.temp/press)"),
    BulkPackagingType(BulkLiquid, "Bulk, liquid"),
    BulkPackagingType(BulkSolidPowders, "Bulk, solid, fine (powders)"),
    BulkPackagingType(BulkSolidGrains, "Bulk, solid, granular (grains)"),
    BulkPackagingType(BulkSolidNodules, "Bulk, solid, large (nodules)"),
    BulkPackagingType(Unpacked, "Unpacked or unpackaged")
  )

  val bulkPackagingTypesRadioOptions: Seq[RadioItem] = Seq(
    RadioItem(content = HtmlContent("Bulk, gas (at 1031 mbar and 15°C) (VG)"), value = Some("VG"), id = Some("value_0")),
    RadioItem(content = HtmlContent("Bulk, liquefied gas (abn.temp/press) (VQ)"), value = Some("VQ"), id = Some("value_1")),
    RadioItem(content = HtmlContent("Bulk, liquid (VL)"), value = Some("VL"), id = Some("value_2")),
    RadioItem(content = HtmlContent("Bulk, solid, fine (powders) (VY)"), value = Some("VY"), id = Some("value_3")),
    RadioItem(content = HtmlContent("Bulk, solid, granular (grains) (VR)"), value = Some("VR"), id = Some("value_4")),
    RadioItem(content = HtmlContent("Bulk, solid, large (nodules) (VO)"), value = Some("VO"), id = Some("value_5")),
    RadioItem(content = HtmlContent("Unpacked or unpackaged (NE)"), value = Some("NE"), id = Some("value_6"))
  )

  val testEpcTobacco: String = "T200"
  val testGoodsTypeTobacco: GoodsType = GoodsType.apply(testEpcTobacco)
  val testCnCodeTobacco: String = "24022090"
  val testCnCodeTobacco2: String = "24029000"
  val testCommodityCodeTobacco: CnCodeInformation = CnCodeInformation(
    cnCode = testCnCodeTobacco,
    cnCodeDescription = "Cigarettes containing tobacco / other",
    exciseProductCode = testEpcTobacco,
    exciseProductCodeDescription = "Cigarettes",
    unitOfMeasure = Kilograms
  )

  val testEpcOtherProductsContainingEthylAlcohol: String = "S500"
  val testCommodityCodeS500: CnCodeInformation = CnCodeInformation(
    cnCode = "10000000",
    cnCodeDescription = "Other products containing ethyl alcohol",
    exciseProductCode = testEpcOtherProductsContainingEthylAlcohol,
    exciseProductCodeDescription = "Other products containing ethyl alcohol",
    unitOfMeasure = Litres20
  )

  val testEpcBeer: String = "B200"
  val testCnCodeBeer: String = "22030001"

  val testEpcSpirit: String = "S200"
  val testCnCodeSpirit: String = "22060031"

  val testEpcEnergy: String = "E500"
  val testCnCodeEnergy: String = "29011000"

  val testEpcEnergyWithDensity: String = "E200"
  val testCnCodeEnergyWithDensity: String = "29011000"

  val testEpcWine: String = "W200"
  val testGoodsTypeWine: GoodsType = GoodsType.apply(testEpcWine)
  val testCnCodeWine: String = "22060031"
  val testCommodityCodeWine: CnCodeInformation = CnCodeInformation(
    cnCode = testCnCodeWine,
    cnCodeDescription = "Sparkling cider and perry",
    exciseProductCode = testEpcWine,
    exciseProductCodeDescription = "Still wine and still fermented beverages other than wine and beer",
    unitOfMeasure = Litres20
  )

  val testItemPackagingTypes: Seq[ItemPackaging] = Seq(
    ItemPackaging("AE", "Aerosol"),
    ItemPackaging("AM", "Ampoule, non protected"),
    ItemPackaging("BG", "Bag"),
    ItemPackaging("VA", "Vat")
  )

  val testItemPackagingTypesJson: JsObject = Json.obj(
    "AE" -> "Aerosol",
    "AM" -> "Ampoule, non protected",
    "BG" -> "Bag",
    "VA" -> "Vat"
  )
  val testExciseProductCodeB000: ExciseProductCode =
    ExciseProductCode(
      "B000",
      "Beer",
      "B",
      "Beer"
    )
  val testExciseProductCodeE200: ExciseProductCode =
    ExciseProductCode(
      "E200",
      "Vegetable and animal oils Products falling within CN codes 1507 to 1518, if these are intended for use as heating fuel or motor fuel (Article 20(1)(a))",
      "E",
      "Energy Products"
    )
  val testExciseProductCodeE470: ExciseProductCode =
    ExciseProductCode(
      "E470",
      "Heavy fuel oil",
      "E",
      "Energy Products"
    )
  val testExciseProductCodeE500: ExciseProductCode =
    ExciseProductCode(
      "E500",
      "Liquified Petroleum gases (LPG) Products falling within CN codes 2711 (except 2711 11, 2711 21 and 2711 29)",
      "E",
      "Energy Products"
    )
  val testExciseProductCodeE600: ExciseProductCode =
    ExciseProductCode(
      "E600",
      "Saturated acyclic hydrocarbons Products falling within CN code 2901 10",
      "E",
      "Energy Products"
    )
  val testExciseProductCodeE800: ExciseProductCode =
    ExciseProductCode(
      "E800",
      "Methanol (methyl alcohol) Products falling within CN code 2905 11 00, which are not of synthetic origin, if these are intended for use as heating fuel or motor fuel",
      "E",
      "Energy Products"
    )
  val testExciseProductCodeE910: ExciseProductCode =
    ExciseProductCode(
      "E910",
      "Fatty-acid mono-alkyl esters, containing by volume 96,5 % or more of esters (FAMAE) falling within CN code 3824 90 99",
      "E",
      "Energy Products"
    )
  val testExciseProductCodeE930: ExciseProductCode =
    ExciseProductCode(
      "E930",
      "Additives falling within CN codes 3811 11, 3811 19 00 and 3811 90 00",
      "E",
      "Energy Products"
    )
  val testExciseProductCodeI200: ExciseProductCode =
    ExciseProductCode(
      "I200",
      "Test Description",
      "I",
      "Test Description"
    )
  val testExciseProductCodeS100: ExciseProductCode =
    ExciseProductCode(
      "S100",
      "Test Description",
      "S",
      "Test Description"
    )
  val testExciseProductCodeS200: ExciseProductCode =
    ExciseProductCode(
      "S200",
      "Spirituous beverages",
      "S",
      "Ethyl alcohol and spirits"
    )
  val testExciseProductCodeS400: ExciseProductCode =
    ExciseProductCode(
      "S400",
      "Partially denatured alcohol",
      "S",
      "Ethyl alcohol and spirits"
    )
  val testExciseProductCodeS500: ExciseProductCode =
    ExciseProductCode(
      "S500",
      "Other products containing ethyl alcohol",
      "S",
      "Ethyl alcohol and spirits"
    )
  val testExciseProductCodeT200: ExciseProductCode =
    ExciseProductCode(
      "T200",
      "Cigarettes",
      "T",
      "Manufactured tobacco products"
    )
  val testExciseProductCodeT300: ExciseProductCode =
    ExciseProductCode(
      "T300",
      "Cigars &amp; cigarillos",
      "T",
      "Manufactured tobacco products"
    )
  val testExciseProductCodeW100: ExciseProductCode =
    ExciseProductCode(
      "W100",
      "Test Description",
      "W",
      "Test Description"
    )
  val testExciseProductCodeW200: ExciseProductCode =
    ExciseProductCode(
      "W200",
      "Still wine and still fermented beverages other than wine and beer",
      "W",
      "Wine and fermented beverages other than wine and beer"
    )
  val testExciseProductCodeW300: ExciseProductCode =
    ExciseProductCode(
      "W300",
      "Sparkling wine and sparkling fermented beverages other than wine and beer",
      "W",
      "Wine and fermented beverages other than wine and beer"
    )

  val testWineOperationsJson = Json.obj(
    "12" -> "Other operations",
    "8" -> "A product harvested during a year other than that indicated in the description has been added to the product",
    "4" -> "The product has been sweetened",
    "5" -> "The product has been fortified for distillation",
    "10" -> "The product has been made on the basis of experimental use of a new oenological practice",
    "0" -> "The product has undergone none of the following operations",
    "2" -> "The product has been acidified",
    "7" -> "A product obtained from a vine variety other than that indicated in the description has been added to the product",
    "3" -> "The product has been de-acidified",
    "11" -> "The product has been partially dealcoholised",
    "9" -> "The product has been made using oak chips",
    "6" -> "A product originating in a geographical unit other than that indicated in the description has been added to the product",
    "1" -> "The product has been enriched"
  )

  val testWineOperations: Seq[WineOperations] = Seq(
    WineOperations("12", "Other operations"),
    WineOperations("8", "A product harvested during a year other than that indicated in the description has been added to the product"),
    WineOperations("4", "The product has been sweetened"),
    WineOperations("5", "The product has been fortified for distillation"),
    WineOperations("10", "The product has been made on the basis of experimental use of a new oenological practice"),
    WineOperations("0", "The product has undergone none of the following operations"),
    WineOperations("2", "The product has been acidified"),
    WineOperations("7", "A product obtained from a vine variety other than that indicated in the description has been added to the product"),
    WineOperations("3", "The product has been de-acidified"),
    WineOperations("11", "The product has been partially dealcoholised"),
    WineOperations("9", "The product has been made using oak chips"),
    WineOperations("6", "A product originating in a geographical unit other than that indicated in the description has been added to the product"),
    WineOperations("1", "The product has been enriched")
  )

  val dispatchOfficeSuffix = "004098"

  val baseFullUserAnswers: UserAnswers = emptyUserAnswers
    // movementType
    .set(DestinationTypePage, MovementScenario.DirectDelivery)
    // consignee
    .set(ConsigneeBusinessNamePage, "consignee name")
    .set(ConsigneeExcisePage, "consignee ern")
    .set(ConsigneeExportVatPage, ConsigneeExportVat(ConsigneeExportVatType.YesEoriNumber, Some("vat no"), Some("consignee eori")))
    .set(ConsigneeAddressPage, testUserAddress.copy(street = "consignee street"))
    // consignor
    .set(ConsignorAddressPage, testUserAddress.copy(street = "consignor street"))
    // complementConsigneeTrader
    .set(ConsigneeExemptOrganisationPage, ExemptOrganisationDetailsModel("state", "number"))
    // deliveryPlaceTrader
    .set(DestinationWarehouseVatPage, "destination ern")
    .set(DestinationBusinessNamePage, "destination name")
    .set(DestinationAddressPage, testUserAddress.copy(street = "destination street"))
    // deliveryPlaceCustomsOffice
    .set(ExportCustomsOfficePage, "delivery place customs office")
    // transportArrangerTrader
    .set(TransportArrangerPage, TransportArranger.GoodsOwner)
    .set(TransportArrangerNamePage, "arranger name")
    .set(TransportArrangerAddressPage, testUserAddress.copy(street = "arranger street"))
    .set(TransportArrangerVatPage, "arranger vat")
    // firstTransporterTrader
    .set(FirstTransporterNamePage, "first name")
    .set(FirstTransporterAddressPage, testUserAddress.copy(street = "first street"))
    .set(FirstTransporterVatPage, "first vat")
    // headerEadEsad
    .set(JourneyTimeHoursPage, 2)
    // transportMode
    .set(HowMovementTransportedPage, HowMovementTransported.AirTransport)
    .set(GiveInformationOtherTransportPage, "info")
    // movementGuarantee
    .set(GuarantorRequiredPage, true)
    .set(GuarantorNamePage, "guarantor name")
    .set(GuarantorAddressPage, testUserAddress.copy(street = "guarantor street"))
    .set(GuarantorVatPage, "guarantor vat")
    .set(GuarantorArrangerPage, GuarantorArranger.GoodsOwner)
    .set(GuarantorNamePage, "guarantor name")
    .set(GuarantorAddressPage, testUserAddress.copy(street = "guarantor street"))
    .set(GuarantorVatPage, "guarantor vat")
    // eadEsadDraft
    .set(LocalReferenceNumberPage(), testLrn)
    .set(InvoiceDetailsPage(), InvoiceDetailsModel("inv ref", LocalDate.parse("2020-12-25")))
    .set(DispatchDetailsPage(), DispatchDetailsModel(LocalDate.parse("2020-10-31"), LocalTime.parse("23:59:59")))
    // transportDetails
    .set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport)
    .set(TransportUnitIdentityPage(testIndex1), "identity")
    .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("seal type", Some("seal info")))
    .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("more info"))

  val xircSubmitChangeDestinationModel: SubmitChangeDestinationModel = SubmitChangeDestinationModel(
    movementType = MovementType.ImportEu,
    attributes = AttributesModel(SubmissionMessageType.Standard, None),
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
      localReferenceNumber = testLrn,
      invoiceNumber = "inv ref",
      invoiceDate = Some("2020-12-25"),
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
    attributes = AttributesModel(SubmissionMessageType.Standard, None),
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
      localReferenceNumber = testLrn,
      invoiceNumber = "inv ref",
      invoiceDate = Some("2020-12-25"),
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
    attributes = AttributesModel(SubmissionMessageType.Standard, None),
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
      localReferenceNumber = testLrn,
      invoiceNumber = "inv ref",
      invoiceDate = Some("2020-12-25"),
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
    attributes = AttributesModel(SubmissionMessageType.Standard, None),
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
      localReferenceNumber = testLrn,
      invoiceNumber = "inv ref",
      invoiceDate = Some("2020-12-25"),
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
}
