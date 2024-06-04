/*
 * Copyright 2024 HM Revenue & Customs
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

package pages.sections.consignee

import base.SpecBase
import models.response.emcsTfe.TraderModel
import models.sections.consignee.ConsigneeExportInformation.{EoriNumber, NoInformation, VatNumber}
import models.sections.info.movementScenario.DestinationType.Export
import play.api.test.FakeRequest

class ConsigneeExportInformationPageSpec extends SpecBase {

  val consignee: TraderModel = maxGetMovementResponse.consigneeTrader.get

  "getValueFromIE801" - {
    "must return Set(VatNumber, EoriNumber)" - {
      "when Consignee exists, has a VAT number and an EORI" in {
        ConsigneeExportInformationPage.getValueFromIE801(dataRequest(FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(
            consigneeTrader = Some(consignee.copy(traderExciseNumber = Some(testVatNumber), eoriNumber = Some(testEoriNumber))),
            destinationType = Export
          ))) mustBe Some(
          Set(VatNumber, EoriNumber)
        )
      }
    }

    "must return Set(VatNumber)" - {
      "when Consignee exists and only has a VAT number (destination type = Export)" in {
        ConsigneeExportInformationPage.getValueFromIE801(dataRequest(FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(
            consigneeTrader = Some(consignee.copy(traderExciseNumber = Some(testVatNumber), eoriNumber = None)),
            destinationType = Export
          ))) mustBe Some(Set(VatNumber))
      }
    }

    "must return Set(EoriNumber)" - {
      "when Consignee exists and only has a EORI number (destination type != Export)" in {
        ConsigneeExportInformationPage.getValueFromIE801(dataRequest(FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(
            consigneeTrader = Some(consignee.copy(traderExciseNumber = Some(testVatNumber), eoriNumber = Some(testEoriNumber))),
          ))) mustBe Some(
          Set(EoriNumber)
        )
      }
    }

    "must return Set(NoInformation)" - {
      "when Consignee exists and has neither a VAT nor a EORI" in {
        ConsigneeExportInformationPage.getValueFromIE801(dataRequest(FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(consigneeTrader = Some(consignee.copy(vatNumber = None, eoriNumber = None))))) mustBe Some(Set(NoInformation))
      }
    }

    "must return None" - {

      "when Consignee doesn't exist" in {
        ConsigneeExportInformationPage.getValueFromIE801(dataRequest(
          FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(consigneeTrader = None)
        )) mustBe None
      }
    }
  }
}
