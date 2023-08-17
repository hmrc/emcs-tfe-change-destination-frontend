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

import models.DestinationType.TaxWarehouse
import models.response.emcsTfe.{AddressModel, ConsignorTraderModel, GetMovementResponse}
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDate

trait GetMovementResponseFixtures { _: BaseFixtures =>

  val getMovementResponseModel: GetMovementResponse = GetMovementResponse(
    arc = testArc,
    sequenceNumber = 1,
    destinationType = TaxWarehouse,
    consigneeTrader = None,
    deliveryPlaceTrader = None,
    localReferenceNumber = "MyLrn",
    eadStatus = "MyEadStatus",
    consignorTrader = ConsignorTraderModel(
      traderExciseNumber = "GB12345GTR144",
      traderName = "MyConsignor",
      address = AddressModel(
        streetNumber = None,
        street = Some("Main101"),
        postcode = Some("ZZ78"),
        city = Some("Zeebrugge")
      )
    ),
    dateOfDispatch = LocalDate.parse("2010-03-04"),
    journeyTime = "MyJourneyTime",
    numberOfItems = 2
  )

  val getMovementResponseInputJson: JsValue = Json.obj(
    "arc" -> testArc,
    "sequenceNumber" -> 1,
    "destinationType" -> "1",
    "localReferenceNumber" -> "MyLrn",
    "eadStatus" -> "MyEadStatus",
    "consignorTrader" -> Json.obj(fields =
      "traderExciseNumber" -> "GB12345GTR144",
      "traderName" -> "MyConsignor",
      "address" -> Json.obj(
        "street" -> "Main101",
        "postcode" -> "ZZ78",
        "city" -> "Zeebrugge"
      )
    ),
    "dateOfDispatch" -> "2010-03-04",
    "journeyTime" -> "MyJourneyTime",
    "items" -> Json.arr(
      Json.obj(fields =
        "itemUniqueReference" -> 1,
        "productCode" -> "W200",
        "cnCode" -> "22041011",
        "quantity" -> 500,
        "grossMass" -> 900,
        "netMass" -> 375,
        "alcoholicStrength" -> 12.7,
        "degreePlato" -> 1.2,
        "fiscalMark" -> "Mark 1",
        "designationOfOrigin" -> "FR",
        "sizeOfProducer" -> "Huge",
        "density" -> 9000,
        "commercialDescription" -> "description",
        "brandNameOfProduct" -> "Big fancy brand name",
        "maturationAge" -> "Lots",
        "packaging" -> Json.arr(
          Json.obj(fields =
            "typeOfPackage" -> "BX",
            "quantity" -> 165,
            "shippingMarks" -> "marks",
            "identityOfCommercialSeal" -> "identity",
            "sealInformation" -> "seal info"
          )
        ),
        "wineProduct" -> Json.obj(fields =
          "wineProductCategory" -> "1",
          "wineGrowingZoneCode" -> "2",
          "thirdCountryOfOrigin" -> "FR",
          "otherInformation" -> "Other info",
          "wineOperations" -> Seq("4", "11", "9")
        )
      ),
      Json.obj(fields =
        "itemUniqueReference" -> 2,
        "productCode" -> "W300",
        "cnCode" -> "22041011",
        "quantity" -> 550,
        "grossMass" -> 910,
        "netMass" -> 315,
        "fiscalMark" -> "Mark 2",
        "designationOfOrigin" -> "FR",
        "sizeOfProducer" -> "Huge",
        "commercialDescription" -> "description",
        "brandNameOfProduct" -> "Big fancy brand name",
        "packaging" -> Json.arr(
          Json.obj(fields =
            "typeOfPackage" -> "BX",
            "quantity" -> 165,
            "shippingMarks" -> "marks",
            "identityOfCommercialSeal" -> "identity",
            "sealInformation" -> "seal info"
          ),
          Json.obj(fields =
            "typeOfPackage" -> "CR",
            "quantity" -> 12
          )
        ),
        "wineProduct" -> Json.obj(fields =
          "wineProductCategory" -> "1",
          "wineGrowingZoneCode" -> "2",
          "thirdCountryOfOrigin" -> "FR",
          "otherInformation" -> "Other info",
          "wineOperations" -> Seq("4", "11", "9")
        )
      )
    ),
    "numberOfItems" -> 2
  )
}
