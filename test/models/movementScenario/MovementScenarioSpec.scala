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

package models.movementScenario

import base.SpecBase
import models.InvalidUserTypeException
import models.requests.UserRequest
import models.movementScenario.MovementScenario._
import play.api.test.FakeRequest

class MovementScenarioSpec extends SpecBase {

  val warehouseKeeperUserRequest: UserRequest[_] = userRequest(FakeRequest()).copy(ern = "GBWK123")
  val registeredConsignorUserRequest: UserRequest[_] = userRequest(FakeRequest()).copy(ern = "GBRC123")
  val nonWKRCUserRequest: UserRequest[_] = userRequest(FakeRequest()).copy(ern = "GB00123")

  "ExportWithCustomsDeclarationLodgedInTheUk" - {
    ".originType" - {
      "when user is a warehouse keeper" - {
        "must return TaxWarehouse" in {
          ExportWithCustomsDeclarationLodgedInTheUk.originType(warehouseKeeperUserRequest) mustBe OriginType.TaxWarehouse
        }
      }
      "when user is a registered consignor" - {
        "must return Imports" in {
          ExportWithCustomsDeclarationLodgedInTheUk.originType(registeredConsignorUserRequest) mustBe OriginType.Imports
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](ExportWithCustomsDeclarationLodgedInTheUk.originType(nonWKRCUserRequest))
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
          ExportWithCustomsDeclarationLodgedInTheUk.movementType(warehouseKeeperUserRequest) mustBe MovementType.DirectExport
        }
      }
      "when user is a registered consignor" - {
        "must return ImportDirectExport" in {
          ExportWithCustomsDeclarationLodgedInTheUk.movementType(registeredConsignorUserRequest) mustBe MovementType.ImportDirectExport
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](ExportWithCustomsDeclarationLodgedInTheUk.movementType(nonWKRCUserRequest))
        }
      }
    }
  }

  "GbTaxWarehouse" - {
    ".originType" - {
      "when user is a warehouse keeper" - {
        "must return TaxWarehouse" in {
          GbTaxWarehouse.originType(warehouseKeeperUserRequest) mustBe OriginType.TaxWarehouse
        }
      }
      "when user is a registered consignor" - {
        "must return Imports" in {
          GbTaxWarehouse.originType(registeredConsignorUserRequest) mustBe OriginType.Imports
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](GbTaxWarehouse.originType(nonWKRCUserRequest))
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
          GbTaxWarehouse.movementType(warehouseKeeperUserRequest) mustBe MovementType.UkToUk
        }
      }
      "when user is a registered consignor" - {
        "must return ImportUk" in {
          GbTaxWarehouse.movementType(registeredConsignorUserRequest) mustBe MovementType.ImportUk
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](GbTaxWarehouse.movementType(nonWKRCUserRequest))
        }
      }
    }
  }

  "DirectDelivery" - {
    ".originType" - {
      "when user is a warehouse keeper" - {
        "must return TaxWarehouse" in {
          DirectDelivery.originType(warehouseKeeperUserRequest) mustBe OriginType.TaxWarehouse
        }
      }
      "when user is a registered consignor" - {
        "must return Imports" in {
          DirectDelivery.originType(registeredConsignorUserRequest) mustBe OriginType.Imports
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](DirectDelivery.originType(nonWKRCUserRequest))
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
          DirectDelivery.movementType(warehouseKeeperUserRequest) mustBe MovementType.UkToEu
        }
      }
      "when user is a registered consignor" - {
        "must return ImportEu" in {
          DirectDelivery.movementType(registeredConsignorUserRequest) mustBe MovementType.ImportEu
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](DirectDelivery.movementType(nonWKRCUserRequest))
        }
      }
    }
  }

  "EuTaxWarehouse" - {
    ".originType" - {
      "when user is a warehouse keeper" - {
        "must return TaxWarehouse" in {
          EuTaxWarehouse.originType(warehouseKeeperUserRequest) mustBe OriginType.TaxWarehouse
        }
      }
      "when user is a registered consignor" - {
        "must return Imports" in {
          EuTaxWarehouse.originType(registeredConsignorUserRequest) mustBe OriginType.Imports
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](EuTaxWarehouse.originType(nonWKRCUserRequest))
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
          EuTaxWarehouse.movementType(warehouseKeeperUserRequest) mustBe MovementType.UkToEu
        }
      }
      "when user is a registered consignor" - {
        "must return ImportEu" in {
          EuTaxWarehouse.movementType(registeredConsignorUserRequest) mustBe MovementType.ImportEu
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](EuTaxWarehouse.movementType(nonWKRCUserRequest))
        }
      }
    }
  }

  "ExemptedOrganisation" - {
    ".originType" - {
      "when user is a warehouse keeper" - {
        "must return TaxWarehouse" in {
          ExemptedOrganisation.originType(warehouseKeeperUserRequest) mustBe OriginType.TaxWarehouse
        }
      }
      "when user is a registered consignor" - {
        "must return Imports" in {
          ExemptedOrganisation.originType(registeredConsignorUserRequest) mustBe OriginType.Imports
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](ExemptedOrganisation.originType(nonWKRCUserRequest))
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
          ExemptedOrganisation.movementType(warehouseKeeperUserRequest) mustBe MovementType.UkToEu
        }
      }
      "when user is a registered consignor" - {
        "must return ImportEu" in {
          ExemptedOrganisation.movementType(registeredConsignorUserRequest) mustBe MovementType.ImportEu
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](ExemptedOrganisation.movementType(nonWKRCUserRequest))
        }
      }
    }
  }

  "ExportWithCustomsDeclarationLodgedInTheEu" - {
    ".originType" - {
      "when user is a warehouse keeper" - {
        "must return TaxWarehouse" in {
          ExportWithCustomsDeclarationLodgedInTheEu.originType(warehouseKeeperUserRequest) mustBe OriginType.TaxWarehouse
        }
      }
      "when user is a registered consignor" - {
        "must return Imports" in {
          ExportWithCustomsDeclarationLodgedInTheEu.originType(registeredConsignorUserRequest) mustBe OriginType.Imports
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](ExportWithCustomsDeclarationLodgedInTheEu.originType(nonWKRCUserRequest))
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
          ExportWithCustomsDeclarationLodgedInTheEu.movementType(warehouseKeeperUserRequest) mustBe MovementType.IndirectExport
        }
      }
      "when user is a registered consignor" - {
        "must return ImportIndirectExport" in {
          ExportWithCustomsDeclarationLodgedInTheEu.movementType(registeredConsignorUserRequest) mustBe MovementType.ImportIndirectExport
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](ExportWithCustomsDeclarationLodgedInTheEu.movementType(nonWKRCUserRequest))
        }
      }
    }
  }

  "RegisteredConsignee" - {
    ".originType" - {
      "when user is a warehouse keeper" - {
        "must return TaxWarehouse" in {
          RegisteredConsignee.originType(warehouseKeeperUserRequest) mustBe OriginType.TaxWarehouse
        }
      }
      "when user is a registered consignor" - {
        "must return Imports" in {
          RegisteredConsignee.originType(registeredConsignorUserRequest) mustBe OriginType.Imports
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](RegisteredConsignee.originType(nonWKRCUserRequest))
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
          RegisteredConsignee.movementType(warehouseKeeperUserRequest) mustBe MovementType.UkToEu
        }
      }
      "when user is a registered consignor" - {
        "must return ImportEu" in {
          RegisteredConsignee.movementType(registeredConsignorUserRequest) mustBe MovementType.ImportEu
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](RegisteredConsignee.movementType(nonWKRCUserRequest))
        }
      }
    }
  }

  "TemporaryRegisteredConsignee" - {
    ".originType" - {
      "when user is a warehouse keeper" - {
        "must return TaxWarehouse" in {
          TemporaryRegisteredConsignee.originType(warehouseKeeperUserRequest) mustBe OriginType.TaxWarehouse
        }
      }
      "when user is a registered consignor" - {
        "must return Imports" in {
          TemporaryRegisteredConsignee.originType(registeredConsignorUserRequest) mustBe OriginType.Imports
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](TemporaryRegisteredConsignee.originType(nonWKRCUserRequest))
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
          TemporaryRegisteredConsignee.movementType(warehouseKeeperUserRequest) mustBe MovementType.UkToEu
        }
      }
      "when user is a registered consignor" - {
        "must return ImportEu" in {
          TemporaryRegisteredConsignee.movementType(registeredConsignorUserRequest) mustBe MovementType.ImportEu
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](TemporaryRegisteredConsignee.movementType(nonWKRCUserRequest))
        }
      }
    }
  }

  "UnknownDestination" - {
    ".originType" - {
      "when user is a warehouse keeper" - {
        "must return TaxWarehouse" in {
          UnknownDestination.originType(warehouseKeeperUserRequest) mustBe OriginType.TaxWarehouse
        }
      }
      "when user is a registered consignor" - {
        "must return Imports" in {
          UnknownDestination.originType(registeredConsignorUserRequest) mustBe OriginType.Imports
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](UnknownDestination.originType(nonWKRCUserRequest))
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
          UnknownDestination.movementType(warehouseKeeperUserRequest) mustBe MovementType.UkToEu
        }
      }
      "when user is a registered consignor" - {
        "must return ImportUnknownDestination" in {
          UnknownDestination.movementType(registeredConsignorUserRequest) mustBe MovementType.ImportUnknownDestination
        }
      }
      "when user is not a warehouse keeper or a registered consignor" - {
        "must return an error" in {
          intercept[InvalidUserTypeException](UnknownDestination.movementType(nonWKRCUserRequest))
        }
      }
    }
  }

}
