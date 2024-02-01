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
      "must return GbTaxWarehouse" - {
        "when deliveryPlaceTrader.traderExciseNumber starts with GB" in {
          MovementScenario.getMovementScenarioFromMovement(testDataRequestWithDeliveryPlaceTrader(
            value = Some("GBWK123"),
            destinationType = DestinationType.TaxWarehouse
          )) mustBe GbTaxWarehouse
        }
        "when deliveryPlaceTrader.traderExciseNumber starts with XI" in {
          MovementScenario.getMovementScenarioFromMovement(testDataRequestWithDeliveryPlaceTrader(
            value = Some("XIWK123"),
            destinationType = DestinationType.TaxWarehouse
          )) mustBe GbTaxWarehouse
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
  }

  "GbTaxWarehouse" - {
    ".originType" - {
      "when user is a warehouse keeper" - {
        "must return TaxWarehouse" in {
          GbTaxWarehouse.originType(warehouseKeeperDataRequest) mustBe OriginType.TaxWarehouse
        }
      }
      "when user is a registered consignor" - {
        "must return Imports" in {
          GbTaxWarehouse.originType(registeredConsignorDataRequest) mustBe OriginType.Imports
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](GbTaxWarehouse.originType(nonWKRCDataRequest))
        }
      }
    }
    ".destinationType" - {
      "must return TaxWarehouse" in {
        GbTaxWarehouse.destinationType mustBe DestinationType.TaxWarehouse
      }
    }
    ".movementType" - {
      "when user is a warehouse keeper" - {
        "must return UkToUk" in {
          GbTaxWarehouse.movementType(warehouseKeeperDataRequest) mustBe MovementType.UkToUk
        }
      }
      "when user is a registered consignor" - {
        "must return ImportUk" in {
          GbTaxWarehouse.movementType(registeredConsignorDataRequest) mustBe MovementType.ImportUk
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](GbTaxWarehouse.movementType(nonWKRCDataRequest))
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
  }

}
