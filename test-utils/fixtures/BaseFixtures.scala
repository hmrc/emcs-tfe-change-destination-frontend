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

import models._
import models.response.SubmitChangeDestinationResponse
import models.response.referenceData.ItemPackaging
import models.sections.consignee.{ConsigneeExportVat, ConsigneeExportVatType}
import models.sections.guarantor.GuarantorArranger
import models.sections.info.movementScenario.{DestinationType, MovementType, OriginType}
import models.sections.info.{DispatchDetailsModel, InvoiceDetailsModel}
import models.sections.journeyType.HowMovementTransported
import models.sections.transportArranger.TransportArranger
import models.submitChangeDestination._
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.Call

import java.time.temporal.ChronoUnit
import java.time.{Instant, LocalDate, LocalDateTime, LocalTime}

trait BaseFixtures {

  val testSessionId: String = "1234-5678-4321"
  val testCredId: String = "credId"
  val testInternalId: String = "internalId"
  val testErn: String = "XIRC123456789"
  val testNorthernIrelandErn = "XIWK123456789"
  val testGreatBritainErn = "GBRC123456789"
  val testLrn: String = "1234567890"
  val testDraftId: String = "arc"
  val testVatNumber: String = "123456789"
  val testExportCustomsOffice: String = "AA123456"
  val testDateOfArrival: LocalDate = LocalDate.now()
  val testSubmissionDate: LocalDateTime = LocalDateTime.now()
  val testConfirmationReference: String = "UYVQBLMXCYK6HAEBZI7TSWAQ6XDTXFYU"
  val testOnwardRoute: Call = Call("GET", "/foo")
  val testId: String = "123"
  val testUrl: String = "testUrl"
  val testBusinessName: String = "testName"
  val testIndex1: Index = Index(0)
  val testIndex2: Index = Index(1)
  val testIndex3: Index = Index(2)
  val testPackagingIndex1: Index = testIndex1
  val testPackagingIndex2: Index = testIndex2
  val testPackagingIndex3: Index = testIndex3

  val testPackageAerosol = ItemPackaging("AE", "Aerosol")
  val testPackageBag = ItemPackaging("BG", "Bag")

  val testExemptedOrganisation = ExemptOrganisationDetailsModel("AT", "12345")
  val testEori = ConsigneeExportVat(ConsigneeExportVatType.YesEoriNumber, None, Some("1234"))
  val testVat = ConsigneeExportVat(ConsigneeExportVatType.YesVatNumber, Some("1234"), None)

  val emptyUserAnswers: UserAnswers = UserAnswers(
    ern = testErn,
    arc = testDraftId,
    lastUpdated = Instant.now().truncatedTo(ChronoUnit.MILLIS)
  )

  val testMinTraderKnownFacts: TraderKnownFacts = TraderKnownFacts(
    traderName = "testTraderName",
    addressLine1 = None,
    addressLine2 = None,
    addressLine3 = None,
    addressLine4 = None,
    addressLine5 = None,
    postcode = None
  )

  val testUserAddress = UserAddress(Some("10"), "Test Street", "Testown", "ZZ1 1ZZ")


  val countryModelAT = CountryModel(
    countryCode = "AT",
    country = "Austria"
  )

  val countryModelBE = CountryModel(
    countryCode = "BE",
    country = "Belgium"
  )

  val countryModelGB = CountryModel(
    countryCode = "GB",
    country = "United Kingdom"
  )

  val countryModelAU = CountryModel(
    countryCode = "AU",
    country = "Australia"
  )

  val countryModelBR = CountryModel(
    countryCode = "BR",
    country = "Brazil"
  )

  val countryJsonAT = Json.obj(
    "countryCode" -> "AT",
    "country" -> "Austria"
  )

  val countryJsonBE = Json.obj(
    "countryCode" -> "BE",
    "country" -> "Belgium"
  )


  val testConsigneeBusinessNameJson: JsObject = Json.obj("consignee" -> Json.obj("businessName" -> "testBusinessName"))

  def testConsigneeExciseJson(ern: String): JsObject = Json.obj("consignee" -> Json.obj("exciseRegistrationNumber" -> ern))

  val testConsigneeAddressJson: JsObject = Json.obj("consignee" -> Json.obj("consigneeAddress" -> testUserAddress))
  val testConsigneeExemptOrganisationJson: JsObject = Json.obj("consignee" -> Json.obj("exemptOrganisation" -> testExemptedOrganisation))
  val testConsigneeVatJson: JsObject = Json.obj("consignee" -> Json.obj("exportVatOrEori" -> testVat))
  val testConsigneeEoriJson: JsObject = Json.obj("consignee" -> Json.obj("exportVatOrEori" -> testEori))

  val invoiceDetailsModel = InvoiceDetailsModel(
    reference = "somereference",
    date = LocalDate.of(2020, 2, 2)
  )

  val invoiceDetailsJson = Json.obj(
    "reference" -> "somereference",
    "date" -> Json.toJson(LocalDate.of(2020, 2, 2))
  )

  val dispatchDetailsModel = DispatchDetailsModel(
    date = LocalDate.of(2020, 2, 2),
    time = LocalTime.of(7, 25)
  )

  val dispatchDetailsJson = Json.obj(
    "date" -> Json.toJson(LocalDate.of(2020, 2, 2)),
    "time" -> "07:25"
  )

  val minimumSubmitChangeDestinationModel: SubmitChangeDestinationModel = SubmitChangeDestinationModel(
    movementType = MovementType.ImportEu,
    attributes = AttributesModel(SubmissionMessageType.Standard, None),
    consigneeTrader = None,
    consignorTrader = TraderModel(
      traderExciseNumber = Some("XIRC123"),
      traderName = Some(testMinTraderKnownFacts.traderName),
      address = None,
      vatNumber = None,
      eoriNumber = None
    ),
    complementConsigneeTrader = None,
    deliveryPlaceTrader = None,
    deliveryPlaceCustomsOffice = None,
    competentAuthorityDispatchOffice = OfficeModel("office"),
    transportArrangerTrader = None,
    firstTransporterTrader = None,
    headerEadEsad = HeaderEadEsadModel(
      destinationType = DestinationType.DirectDelivery,
      journeyTime = "2 hours",
      transportArrangement = TransportArranger.GoodsOwner
    ),
    transportMode = TransportModeModel(
      transportModeCode = HowMovementTransported.AirTransport.toString,
      complementaryInformation = None
    ),
    movementGuarantee = MovementGuaranteeModel(
      guarantorTypeCode = GuarantorArranger.GoodsOwner,
      guarantorTrader = None
    ),
    eadEsadDraft = EadEsadDraftModel(
      localReferenceNumber = testLrn,
      invoiceNumber = "inv ref",
      invoiceDate = None,
      originTypeCode = OriginType.Imports,
      dateOfDispatch = "2020-10-31",
      timeOfDispatch = None
    ),
    transportDetails = Seq()
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
