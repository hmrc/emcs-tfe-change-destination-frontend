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

package models.sections.info.movementScenario

import base.SpecBase
import models.requests.DataRequest
import models.response.InvalidUserTypeException
import models.sections.info.movementScenario.MovementScenario._
import play.api.test.FakeRequest

class MovementScenarioSpec extends SpecBase {

  val dutyPaidDataRequest: DataRequest[_] = dataRequest(FakeRequest(), ern = testNorthernIrelandDutyPaidErn)
  val warehouseKeeperDataRequest: DataRequest[_] = dataRequest(FakeRequest(), ern = "GBWK123")
  val registeredConsignorDataRequest: DataRequest[_] = dataRequest(FakeRequest(), ern = "GBRC123")
  val nonWKRCDataRequest: DataRequest[_] = dataRequest(FakeRequest(), ern = "GB00123")

  "getMovementScenarioFromMovement" - {
    def testDataRequestWithDeliveryPlaceCustomsOffice(value: Option[String], destinationType: DestinationType): DataRequest[_] =
      dataRequest(FakeRequest(), movementDetails = maxGetMovementResponse
        .copy(
          deliveryPlaceCustomsOfficeReferenceNumber = value,
          destinationType = destinationType,
          headerEadEsad = maxGetMovementResponse.headerEadEsad.copy(destinationType = destinationType)
        )
      )

    def testDataRequestWithDeliveryPlaceTrader(value: Option[String], destinationType: DestinationType): DataRequest[_] =
      dataRequest(FakeRequest(), movementDetails =
        maxGetMovementResponse
          .copy(
            destinationType = destinationType,
            deliveryPlaceTrader = maxGetMovementResponse.deliveryPlaceTrader.map(_.copy(traderExciseNumber = value)),
            headerEadEsad = maxGetMovementResponse.headerEadEsad.copy(destinationType = destinationType)
          )
      )

    def testDataRequest(destinationType: DestinationType): DataRequest[_] =
      dataRequest(FakeRequest(), movementDetails =
        maxGetMovementResponse
          .copy(
            destinationType = destinationType,
            headerEadEsad = maxGetMovementResponse.headerEadEsad.copy(destinationType = destinationType)
          )
      )

    "when DestinationType is Export" - {
      "must return ExportWithCustomsDeclarationLodgedInTheUk" - {
        "when deliveryPlaceCustomsOfficeReferenceNumber starts with GB" in {
          MovementScenario.getMovementScenarioFromMovement(testDataRequestWithDeliveryPlaceCustomsOffice(
            value = Some("GBWK123"),
            destinationType = DestinationType.Export
          )) mustBe ExportWithCustomsDeclarationLodgedInTheUk
        }
        "when deliveryPlaceCustomsOfficeReferenceNumber starts with XI" in {
          MovementScenario.getMovementScenarioFromMovement(testDataRequestWithDeliveryPlaceCustomsOffice(
            value = Some("XIWK123"),
            destinationType = DestinationType.Export
          )) mustBe ExportWithCustomsDeclarationLodgedInTheUk
        }
      }
      "must return ExportWithCustomsDeclarationLodgedInTheEu" - {
        "when deliveryPlaceCustomsOfficeReferenceNumber does not start with GB or XI" in {
          MovementScenario.getMovementScenarioFromMovement(testDataRequestWithDeliveryPlaceCustomsOffice(
            value = Some("FRWK123"),
            destinationType = DestinationType.Export
          )) mustBe ExportWithCustomsDeclarationLodgedInTheEu
        }
        "when deliveryPlaceCustomsOfficeReferenceNumber is not present" in {
          MovementScenario.getMovementScenarioFromMovement(testDataRequestWithDeliveryPlaceCustomsOffice(
            value = None,
            destinationType = DestinationType.Export
          )) mustBe ExportWithCustomsDeclarationLodgedInTheEu
        }
      }
    }
    "when DestinationType is TaxWarehouse" - {
      "must return TaxWarehouse.GB" - {
        "when deliveryPlaceTrader.traderExciseNumber starts with GB" in {
          MovementScenario.getMovementScenarioFromMovement(testDataRequestWithDeliveryPlaceTrader(
            value = Some("GBWK123"),
            destinationType = DestinationType.TaxWarehouse
          )) mustBe UkTaxWarehouse.GB
        }
      }
      "must return TaxWarehouse.XI" - {
        "when deliveryPlaceTrader.traderExciseNumber starts with XI" in {
          MovementScenario.getMovementScenarioFromMovement(testDataRequestWithDeliveryPlaceTrader(
            value = Some("XIWK123"),
            destinationType = DestinationType.TaxWarehouse
          )) mustBe UkTaxWarehouse.NI
        }
      }
      "must return EuTaxWarehouse" - {
        "when deliveryPlaceTrader.traderExciseNumber does not start with GB or XI" in {
          MovementScenario.getMovementScenarioFromMovement(testDataRequestWithDeliveryPlaceTrader(
            value = Some("FRWK123"),
            destinationType = DestinationType.TaxWarehouse
          )) mustBe EuTaxWarehouse
        }
        "when deliveryPlaceTrader.traderExciseNumber is not present" in {
          MovementScenario.getMovementScenarioFromMovement(testDataRequestWithDeliveryPlaceTrader(
            value = None,
            destinationType = DestinationType.TaxWarehouse
          )) mustBe EuTaxWarehouse
        }
      }
    }
    "when DestinationType is DirectDelivery" - {
      "must return DirectDelivery" in {
        MovementScenario.getMovementScenarioFromMovement(testDataRequest(
          destinationType = DestinationType.DirectDelivery
        )) mustBe DirectDelivery
      }
    }
    "when DestinationType is ExemptedOrganisation" - {
      "must return ExemptedOrganisation" in {
        MovementScenario.getMovementScenarioFromMovement(testDataRequest(
          destinationType = DestinationType.ExemptedOrganisation
        )) mustBe ExemptedOrganisation
      }
    }
    "when DestinationType is RegisteredConsignee" - {
      "must return RegisteredConsignee" in {
        MovementScenario.getMovementScenarioFromMovement(testDataRequest(
          destinationType = DestinationType.RegisteredConsignee
        )) mustBe RegisteredConsignee
      }
    }
    "when DestinationType is TemporaryRegisteredConsignee" - {
      "must return TemporaryRegisteredConsignee" in {
        MovementScenario.getMovementScenarioFromMovement(testDataRequest(
          destinationType = DestinationType.TemporaryRegisteredConsignee
        )) mustBe TemporaryRegisteredConsignee
      }
    }
    "when DestinationType is UnknownDestination" - {
      "must return UnknownDestination" in {
        MovementScenario.getMovementScenarioFromMovement(testDataRequest(
          destinationType = DestinationType.UnknownDestination
        )) mustBe UnknownDestination
      }
    }
    "when DestinationType is a Duty Paid option" - {
      "when DestinationType is CertifiedConsignee" in {
        val result =
          MovementScenario.getMovementScenarioFromMovement(testDataRequest(
            destinationType = DestinationType.CertifiedConsignee
          ))

        result mustBe CertifiedConsignee
      }
      "when DestinationType is TemporaryCertifiedConsignee" in {
        val result =
          MovementScenario.getMovementScenarioFromMovement(testDataRequest(
            destinationType = DestinationType.TemporaryCertifiedConsignee
          ))
        result mustBe TemporaryCertifiedConsignee
      }
      "when DestinationType is ReturnToThePlaceOfDispatchOfTheConsignor" in {
        val result =
          MovementScenario.getMovementScenarioFromMovement(testDataRequest(
            destinationType = DestinationType.ReturnToThePlaceOfDispatchOfTheConsignor
          ))

        result mustBe ReturnToThePlaceOfDispatch
      }
    }
  }

  "ExportWithCustomsDeclarationLodgedInTheUk" - {
    ".originType" - {
      "when user is a warehouse keeper" - {
        "must return TaxWarehouse" in {
          ExportWithCustomsDeclarationLodgedInTheUk.originType(warehouseKeeperDataRequest) mustBe OriginType.TaxWarehouse
        }
      }
      "when user is a registered consignor" - {
        "must return Imports" in {
          ExportWithCustomsDeclarationLodgedInTheUk.originType(registeredConsignorDataRequest) mustBe OriginType.Imports
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](ExportWithCustomsDeclarationLodgedInTheUk.originType(nonWKRCDataRequest))
        }
      }
    }
    ".destinationType" - {
      "must return Export" in {
        ExportWithCustomsDeclarationLodgedInTheUk.destinationType mustBe DestinationType.Export
      }
    }
    ".movementType" - {
      "when user is a warehouse keeper" - {
        "must return DirectExport" in {
          ExportWithCustomsDeclarationLodgedInTheUk.movementType(warehouseKeeperDataRequest) mustBe MovementType.DirectExport
        }
      }
      "when user is a registered consignor" - {
        "must return ImportDirectExport" in {
          ExportWithCustomsDeclarationLodgedInTheUk.movementType(registeredConsignorDataRequest) mustBe MovementType.ImportDirectExport
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](ExportWithCustomsDeclarationLodgedInTheUk.movementType(nonWKRCDataRequest))
        }
      }
    }
    ".isExport" - {
      "must return true" in {
        ExportWithCustomsDeclarationLodgedInTheUk.isExport mustBe true
      }
    }
    ".isNItoEU" - {
      "when NI ern" - {
        "must return false" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testNorthernIrelandWarehouseKeeperErn)
          ExportWithCustomsDeclarationLodgedInTheUk.isNItoEU mustBe false
        }
      }
      "when not NI ern" - {
        "must return false" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testGreatBritainErn)
          ExportWithCustomsDeclarationLodgedInTheUk.isNItoEU mustBe false
        }
      }
    }
  }

  "UkTaxWarehouse.GB" - {
    ".originType" - {
      "when user is a warehouse keeper" - {
        "must return TaxWarehouse" in {
          UkTaxWarehouse.GB.originType(warehouseKeeperDataRequest) mustBe OriginType.TaxWarehouse
        }
      }
      "when user is a registered consignor" - {
        "must return Imports" in {
          UkTaxWarehouse.GB.originType(registeredConsignorDataRequest) mustBe OriginType.Imports
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](UkTaxWarehouse.GB.originType(nonWKRCDataRequest))
        }
      }
    }
    ".destinationType" - {
      "must return TaxWarehouse" in {
        UkTaxWarehouse.GB.destinationType mustBe DestinationType.TaxWarehouse
      }
    }
    ".movementType" - {
      "when user is a warehouse keeper" - {
        "must return UkToUk" in {
          UkTaxWarehouse.GB.movementType(warehouseKeeperDataRequest) mustBe MovementType.UkToUk
        }
      }
      "when user is a registered consignor" - {
        "must return ImportUk" in {
          UkTaxWarehouse.GB.movementType(registeredConsignorDataRequest) mustBe MovementType.ImportUk
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](UkTaxWarehouse.GB.movementType(nonWKRCDataRequest))
        }
      }
    }
    ".isExport" - {
      "must return false" in {
        UkTaxWarehouse.GB.isExport mustBe false
      }
    }
    ".isNItoEU" - {
      "when NI ern" - {
        "must return false" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testNorthernIrelandWarehouseKeeperErn)
          UkTaxWarehouse.GB.isNItoEU mustBe false
        }
      }
      "when not NI ern" - {
        "must return false" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testGreatBritainErn)
          UkTaxWarehouse.GB.isNItoEU mustBe false
        }
      }
    }
  }

  "UkTaxWarehouse.NI" - {
    ".originType" - {
      "when user is a warehouse keeper" - {
        "must return TaxWarehouse" in {
          UkTaxWarehouse.NI.originType(warehouseKeeperDataRequest) mustBe OriginType.TaxWarehouse
        }
      }
      "when user is a registered consignor" - {
        "must return Imports" in {
          UkTaxWarehouse.NI.originType(registeredConsignorDataRequest) mustBe OriginType.Imports
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](UkTaxWarehouse.NI.originType(nonWKRCDataRequest))
        }
      }
    }
    ".destinationType" - {
      "must return TaxWarehouse" in {
        UkTaxWarehouse.NI.destinationType mustBe DestinationType.TaxWarehouse
      }
    }
    ".movementType" - {
      "when user is a warehouse keeper" - {
        "must return UkToUk" in {
          UkTaxWarehouse.NI.movementType(warehouseKeeperDataRequest) mustBe MovementType.UkToUk
        }
      }
      "when user is a registered consignor" - {
        "must return ImportUk" in {
          UkTaxWarehouse.NI.movementType(registeredConsignorDataRequest) mustBe MovementType.ImportUk
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](UkTaxWarehouse.NI.movementType(nonWKRCDataRequest))
        }
      }
    }
    ".isExport" - {
      "must return false" in {
        UkTaxWarehouse.NI.isExport mustBe false
      }
    }
    ".isNItoEU" - {
      "when NI ern" - {
        "must return false" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testNorthernIrelandWarehouseKeeperErn)
          UkTaxWarehouse.NI.isNItoEU mustBe false
        }
      }
      "when not NI ern" - {
        "must return false" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testGreatBritainErn)
          UkTaxWarehouse.NI.isNItoEU mustBe false
        }
      }
    }
  }

  "DirectDelivery" - {
    ".originType" - {
      "when user is a warehouse keeper" - {
        "must return TaxWarehouse" in {
          DirectDelivery.originType(warehouseKeeperDataRequest) mustBe OriginType.TaxWarehouse
        }
      }
      "when user is a registered consignor" - {
        "must return Imports" in {
          DirectDelivery.originType(registeredConsignorDataRequest) mustBe OriginType.Imports
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](DirectDelivery.originType(nonWKRCDataRequest))
        }
      }
    }
    ".destinationType" - {
      "must return DirectDelivery" in {
        DirectDelivery.destinationType mustBe DestinationType.DirectDelivery
      }
    }
    ".movementType" - {
      "when user is a warehouse keeper" - {
        "must return UkToEu" in {
          DirectDelivery.movementType(warehouseKeeperDataRequest) mustBe MovementType.UkToEu
        }
      }
      "when user is a registered consignor" - {
        "must return ImportEu" in {
          DirectDelivery.movementType(registeredConsignorDataRequest) mustBe MovementType.ImportEu
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](DirectDelivery.movementType(nonWKRCDataRequest))
        }
      }
    }
    ".isExport" - {
      "must return false" in {
        DirectDelivery.isExport mustBe false
      }
    }
    ".isNItoEU" - {
      "when NI ern" - {
        "must return true" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testNorthernIrelandWarehouseKeeperErn)
          DirectDelivery.isNItoEU mustBe true
        }
      }
      "when not NI ern" - {
        "must return false" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testGreatBritainErn)
          DirectDelivery.isNItoEU mustBe false
        }
      }
    }
  }

  "EuTaxWarehouse" - {
    ".originType" - {
      "when user is a warehouse keeper" - {
        "must return TaxWarehouse" in {
          EuTaxWarehouse.originType(warehouseKeeperDataRequest) mustBe OriginType.TaxWarehouse
        }
      }
      "when user is a registered consignor" - {
        "must return Imports" in {
          EuTaxWarehouse.originType(registeredConsignorDataRequest) mustBe OriginType.Imports
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](EuTaxWarehouse.originType(nonWKRCDataRequest))
        }
      }
    }
    ".destinationType" - {
      "must return TaxWarehouse" in {
        EuTaxWarehouse.destinationType mustBe DestinationType.TaxWarehouse
      }
    }
    ".movementType" - {
      "when user is a warehouse keeper" - {
        "must return UkToEu" in {
          EuTaxWarehouse.movementType(warehouseKeeperDataRequest) mustBe MovementType.UkToEu
        }
      }
      "when user is a registered consignor" - {
        "must return ImportEu" in {
          EuTaxWarehouse.movementType(registeredConsignorDataRequest) mustBe MovementType.ImportEu
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](EuTaxWarehouse.movementType(nonWKRCDataRequest))
        }
      }
    }
    ".isExport" - {
      "must return false" in {
        EuTaxWarehouse.isExport mustBe false
      }
    }
    ".isNItoEU" - {
      "when NI ern" - {
        "must return true" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testNorthernIrelandWarehouseKeeperErn)
          EuTaxWarehouse.isNItoEU mustBe true
        }
      }
      "when not NI ern" - {
        "must return false" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testGreatBritainErn)
          EuTaxWarehouse.isNItoEU mustBe false
        }
      }
    }
  }

  "ExemptedOrganisation" - {
    ".originType" - {
      "when user is a warehouse keeper" - {
        "must return TaxWarehouse" in {
          ExemptedOrganisation.originType(warehouseKeeperDataRequest) mustBe OriginType.TaxWarehouse
        }
      }
      "when user is a registered consignor" - {
        "must return Imports" in {
          ExemptedOrganisation.originType(registeredConsignorDataRequest) mustBe OriginType.Imports
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](ExemptedOrganisation.originType(nonWKRCDataRequest))
        }
      }
    }
    ".destinationType" - {
      "must return ExemptedOrganisation" in {
        ExemptedOrganisation.destinationType mustBe DestinationType.ExemptedOrganisation
      }
    }
    ".movementType" - {
      "when user is a warehouse keeper" - {
        "must return UkToEu" in {
          ExemptedOrganisation.movementType(warehouseKeeperDataRequest) mustBe MovementType.UkToEu
        }
      }
      "when user is a registered consignor" - {
        "must return ImportEu" in {
          ExemptedOrganisation.movementType(registeredConsignorDataRequest) mustBe MovementType.ImportEu
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](ExemptedOrganisation.movementType(nonWKRCDataRequest))
        }
      }
    }
    ".isExport" - {
      "must return false" in {
        ExemptedOrganisation.isExport mustBe false
      }
    }
    ".isNItoEU" - {
      "when NI ern" - {
        "must return true" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testNorthernIrelandWarehouseKeeperErn)
          ExemptedOrganisation.isNItoEU mustBe true
        }
      }
      "when not NI ern" - {
        "must return false" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testGreatBritainErn)
          ExemptedOrganisation.isNItoEU mustBe false
        }
      }
    }
  }

  "ExportWithCustomsDeclarationLodgedInTheEu" - {
    ".originType" - {
      "when user is a warehouse keeper" - {
        "must return TaxWarehouse" in {
          ExportWithCustomsDeclarationLodgedInTheEu.originType(warehouseKeeperDataRequest) mustBe OriginType.TaxWarehouse
        }
      }
      "when user is a registered consignor" - {
        "must return Imports" in {
          ExportWithCustomsDeclarationLodgedInTheEu.originType(registeredConsignorDataRequest) mustBe OriginType.Imports
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](ExportWithCustomsDeclarationLodgedInTheEu.originType(nonWKRCDataRequest))
        }
      }
    }
    ".destinationType" - {
      "must return Export" in {
        ExportWithCustomsDeclarationLodgedInTheEu.destinationType mustBe DestinationType.Export
      }
    }
    ".movementType" - {
      "when user is a warehouse keeper" - {
        "must return IndirectExport" in {
          ExportWithCustomsDeclarationLodgedInTheEu.movementType(warehouseKeeperDataRequest) mustBe MovementType.IndirectExport
        }
      }
      "when user is a registered consignor" - {
        "must return ImportIndirectExport" in {
          ExportWithCustomsDeclarationLodgedInTheEu.movementType(registeredConsignorDataRequest) mustBe MovementType.ImportIndirectExport
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](ExportWithCustomsDeclarationLodgedInTheEu.movementType(nonWKRCDataRequest))
        }
      }
    }
    ".isExport" - {
      "must return false" in {
        ExemptedOrganisation.isExport mustBe false
      }
    }
    ".isNItoEU" - {
      "when NI ern" - {
        "must return true" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testNorthernIrelandWarehouseKeeperErn)
          ExemptedOrganisation.isNItoEU mustBe true
        }
      }
      "when not NI ern" - {
        "must return false" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testGreatBritainErn)
          ExemptedOrganisation.isNItoEU mustBe false
        }
      }
    }
  }

  "RegisteredConsignee" - {
    ".originType" - {
      "when user is a warehouse keeper" - {
        "must return TaxWarehouse" in {
          RegisteredConsignee.originType(warehouseKeeperDataRequest) mustBe OriginType.TaxWarehouse
        }
      }
      "when user is a registered consignor" - {
        "must return Imports" in {
          RegisteredConsignee.originType(registeredConsignorDataRequest) mustBe OriginType.Imports
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](RegisteredConsignee.originType(nonWKRCDataRequest))
        }
      }
    }
    ".destinationType" - {
      "must return RegisteredConsignee" in {
        RegisteredConsignee.destinationType mustBe DestinationType.RegisteredConsignee
      }
    }
    ".movementType" - {
      "when user is a warehouse keeper" - {
        "must return UkToEu" in {
          RegisteredConsignee.movementType(warehouseKeeperDataRequest) mustBe MovementType.UkToEu
        }
      }
      "when user is a registered consignor" - {
        "must return ImportEu" in {
          RegisteredConsignee.movementType(registeredConsignorDataRequest) mustBe MovementType.ImportEu
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](RegisteredConsignee.movementType(nonWKRCDataRequest))
        }
      }
    }
    ".isExport" - {
      "must return false" in {
        RegisteredConsignee.isExport mustBe false
      }
    }
    ".isNItoEU" - {
      "when NI ern" - {
        "must return true" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testNorthernIrelandWarehouseKeeperErn)
          RegisteredConsignee.isNItoEU mustBe true
        }
      }
      "when not NI ern" - {
        "must return false" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testGreatBritainErn)
          RegisteredConsignee.isNItoEU mustBe false
        }
      }
    }
  }

  "TemporaryRegisteredConsignee" - {
    ".originType" - {
      "when user is a warehouse keeper" - {
        "must return TaxWarehouse" in {
          TemporaryRegisteredConsignee.originType(warehouseKeeperDataRequest) mustBe OriginType.TaxWarehouse
        }
      }
      "when user is a registered consignor" - {
        "must return Imports" in {
          TemporaryRegisteredConsignee.originType(registeredConsignorDataRequest) mustBe OriginType.Imports
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](TemporaryRegisteredConsignee.originType(nonWKRCDataRequest))
        }
      }
    }
    ".destinationType" - {
      "must return TemporaryRegisteredConsignee" in {
        TemporaryRegisteredConsignee.destinationType mustBe DestinationType.TemporaryRegisteredConsignee
      }
    }
    ".movementType" - {
      "when user is a warehouse keeper" - {
        "must return UkToEu" in {
          TemporaryRegisteredConsignee.movementType(warehouseKeeperDataRequest) mustBe MovementType.UkToEu
        }
      }
      "when user is a registered consignor" - {
        "must return ImportEu" in {
          TemporaryRegisteredConsignee.movementType(registeredConsignorDataRequest) mustBe MovementType.ImportEu
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](TemporaryRegisteredConsignee.movementType(nonWKRCDataRequest))
        }
      }
    }
    ".isExport" - {
      "must return false" in {
        TemporaryRegisteredConsignee.isExport mustBe false
      }
    }
    ".isNItoEU" - {
      "when NI ern" - {
        "must return true" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testNorthernIrelandWarehouseKeeperErn)
          TemporaryRegisteredConsignee.isNItoEU mustBe true
        }
      }
      "when not NI ern" - {
        "must return false" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testGreatBritainErn)
          TemporaryRegisteredConsignee.isNItoEU mustBe false
        }
      }
    }
  }

  "UnknownDestination" - {
    ".originType" - {
      "when user is a warehouse keeper" - {
        "must return TaxWarehouse" in {
          UnknownDestination.originType(warehouseKeeperDataRequest) mustBe OriginType.TaxWarehouse
        }
      }
      "when user is a registered consignor" - {
        "must return Imports" in {
          UnknownDestination.originType(registeredConsignorDataRequest) mustBe OriginType.Imports
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](UnknownDestination.originType(nonWKRCDataRequest))
        }
      }
    }
    ".destinationType" - {
      "must return UnknownDestination" in {
        UnknownDestination.destinationType mustBe DestinationType.UnknownDestination
      }
    }
    ".movementType" - {
      "when user is a warehouse keeper" - {
        "must return UkToEu" in {
          UnknownDestination.movementType(warehouseKeeperDataRequest) mustBe MovementType.UkToEu
        }
      }
      "when user is a registered consignor" - {
        "must return ImportUnknownDestination" in {
          UnknownDestination.movementType(registeredConsignorDataRequest) mustBe MovementType.ImportUnknownDestination
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](UnknownDestination.movementType(nonWKRCDataRequest))
        }
      }
    }
    ".isExport" - {
      "must return false" in {
        UnknownDestination.isExport mustBe false
      }
    }
    ".isNItoEU" - {
      "when NI ern" - {
        "must return false" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testNorthernIrelandWarehouseKeeperErn)
          UnknownDestination.isNItoEU mustBe false
        }
      }
      "when not NI ern" - {
        "must return false" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testGreatBritainErn)
          UnknownDestination.isNItoEU mustBe false
        }
      }
    }
  }

  "CertifiedConsignee" - {
    ".originType" - {
      "must return DutyPaid" in {
        CertifiedConsignee.originType(dutyPaidDataRequest) mustBe OriginType.DutyPaid
      }
    }
    ".destinationType" - {
      "must return CertifiedConsignee" in {
        CertifiedConsignee.destinationType mustBe DestinationType.CertifiedConsignee
      }
    }
    ".movementType" - {
      "when user is Duty Paid" - {
        "must return UkToEu" in {
          CertifiedConsignee.movementType(dutyPaidDataRequest) mustBe MovementType.UkToEu
        }
      }
      "when user is not Duty Paid" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](CertifiedConsignee.movementType(nonWKRCDataRequest))
        }
      }
    }
    ".isExport" - {
      "must return false" in {
        CertifiedConsignee.isExport mustBe false
      }
    }
    ".isNItoEU" - {
      "when NI ern" - {
        "must return true" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testNorthernIrelandWarehouseKeeperErn)
          CertifiedConsignee.isNItoEU mustBe true
        }
      }
      "when not NI ern" - {
        "must return false" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testGreatBritainErn)
          CertifiedConsignee.isNItoEU mustBe false
        }
      }
    }
  }

  "TemporaryCertifiedConsignee" - {
    ".originType" - {
      "must return DutyPaid" in {
        TemporaryCertifiedConsignee.originType(dutyPaidDataRequest) mustBe OriginType.DutyPaid
      }
    }
    ".destinationType" - {
      "must return TemporaryCertifiedConsignee" in {
        TemporaryCertifiedConsignee.destinationType mustBe DestinationType.TemporaryCertifiedConsignee
      }
    }
    ".movementType" - {
      "when user is Duty Paid" - {
        "must return UkToEu" in {
          TemporaryCertifiedConsignee.movementType(dutyPaidDataRequest) mustBe MovementType.UkToEu
        }
      }
      "when user is not Duty Paid" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](TemporaryCertifiedConsignee.movementType(nonWKRCDataRequest))
        }
      }
    }
    ".isExport" - {
      "must return false" in {
        TemporaryCertifiedConsignee.isExport mustBe false
      }
    }
    ".isNItoEU" - {
      "when NI ern" - {
        "must return true" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testNorthernIrelandWarehouseKeeperErn)
          TemporaryCertifiedConsignee.isNItoEU mustBe true
        }
      }
      "when not NI ern" - {
        "must return false" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testGreatBritainErn)
          TemporaryCertifiedConsignee.isNItoEU mustBe false
        }
      }
    }
  }

  "ReturnToThePlaceOfDispatch" - {
    ".originType" - {
      "must return DutyPaid" in {
        ReturnToThePlaceOfDispatch.originType(dutyPaidDataRequest) mustBe OriginType.DutyPaid
      }
    }
    ".destinationType" - {
      "must return ReturnToThePlaceOfDispatchOfTheConsignor" in {
        ReturnToThePlaceOfDispatch.destinationType mustBe DestinationType.ReturnToThePlaceOfDispatchOfTheConsignor
      }
    }
    ".movementType" - {
      "when user is Duty Paid" - {
        "must return UkToEu" in {
          ReturnToThePlaceOfDispatch.movementType(dutyPaidDataRequest) mustBe MovementType.UkToEu
        }
      }
      "when user is not Duty Paid" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](ReturnToThePlaceOfDispatch.movementType(nonWKRCDataRequest))
        }
      }
    }
    ".isExport" - {
      "must return false" in {
        ReturnToThePlaceOfDispatch.isExport mustBe false
      }
    }
    ".isNItoEU" - {
      "when NI ern" - {
        "must return false" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testNorthernIrelandWarehouseKeeperErn)
          ReturnToThePlaceOfDispatch.isNItoEU mustBe false
        }
      }
      "when not NI ern" - {
        "must return false" in {
          implicit val dr = dataRequest(FakeRequest(), ern = testGreatBritainErn)
          ReturnToThePlaceOfDispatch.isNItoEU mustBe false
        }
      }
    }
  }
}
