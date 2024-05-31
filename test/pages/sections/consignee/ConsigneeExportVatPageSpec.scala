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
import models.sections.info.ChangeType.ChangeConsignee
import models.sections.info.movementScenario.DestinationType.{Export, TaxWarehouse}
import pages.sections.info.ChangeTypePage
import play.api.test.FakeRequest

class ConsigneeExportVatPageSpec extends SpecBase {

  val consignee: TraderModel = maxGetMovementResponse.consigneeTrader.get

  "getValueFromIE801" - {
    "must return the VAT number" - {
      "when destination type is Export" in {
        ConsigneeExportVatPage.getValueFromIE801(dataRequest(FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(destinationType = Export))) mustBe Some("ConsigneeTraderId")
      }
    }

    "must return None" - {

      "when the user wants to change the consignee" in {
        ConsigneeExportVatPage.getValueFromIE801(dataRequest(
          FakeRequest(),
          emptyUserAnswers.set(ChangeTypePage, ChangeConsignee)
        )) mustBe None
      }

      "when the destination type is not Export" in {
        ConsigneeExportVatPage.getValueFromIE801(dataRequest(
          FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(destinationType = TaxWarehouse))
        ) mustBe None
      }
    }
  }
}
