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
import models.sections.consignee.{ConsigneeExportInformation, ConsigneeExportInformationType}
import play.api.test.FakeRequest

class ConsigneeExportInformationPageSpec extends SpecBase {

  val consignee: TraderModel = maxGetMovementResponse.consigneeTrader.get

  "getValueFromIE801" - {
    "must return Some(ConsigneeExportInformation)" - {
      "when Consignee exists and has a VAT number" in {
        ConsigneeExportInformationPage.getValueFromIE801(dataRequest(FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(consigneeTrader = Some(consignee.copy(vatNumber = Some("123456789"), eoriNumber = None))))) mustBe Some(
          ConsigneeExportInformation(ConsigneeExportInformationType.YesVatNumber, Some("123456789"), None)
        )
      }
    }

    "must return Some(ConsigneeExportInformation(YesEoriNumber)" - {
      "when Consignee exists and has a EORI number" in {
        ConsigneeExportInformationPage.getValueFromIE801(dataRequest(FakeRequest())) mustBe Some(
          ConsigneeExportInformation(ConsigneeExportInformationType.YesEoriNumber, None, consignee.eoriNumber)
        )
      }
    }

    "must return Some(ConsigneeExportInformation(No))" - {
      "when Consignee exists and has neither a VAT nor a EORI" in {
        ConsigneeExportInformationPage.getValueFromIE801(dataRequest(FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(consigneeTrader = Some(consignee.copy(vatNumber = None, eoriNumber = None))))) mustBe Some(ConsigneeExportInformation(ConsigneeExportInformationType.No, None, None))
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
