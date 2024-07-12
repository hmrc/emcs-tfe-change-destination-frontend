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

package pages.sections.guarantor

import base.SpecBase
import models.response.emcsTfe.TraderModel
import models.sections.info.movementScenario.DestinationType.TaxWarehouse
import play.api.test.FakeRequest

class GuarantorVatPageSpec extends SpecBase {

  val guarantorTraders: Seq[TraderModel] = maxGetMovementResponse.movementGuarantee.guarantorTrader.get

  "getValueFromIE801" - {
    "must return Some(_)" - {
      "when guarantor trader is defined and has a VAT number (getting the first guarantor)" in {
        GuarantorVatPage.getValueFromIE801(dataRequest(
          FakeRequest(),
          ern = testGreatBritainWarehouseErn,
          movementDetails = maxGetMovementResponse.copy(destinationType = TaxWarehouse)
        )) mustBe guarantorTraders.headOption.flatMap(_.vatNumber)
      }
    }
    "must return None" - {
      "when guarantor trader is defined and has no vat number" in {
        GuarantorVatPage.getValueFromIE801(dataRequest(
          FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(movementGuarantee = maxGetMovementResponse.movementGuarantee.copy(guarantorTrader = Some(guarantorTraders.map(_.copy(vatNumber = None)))))
        )) mustBe None
      }
      "when guarantor trader trader doesn't exist" in {
        GuarantorVatPage.getValueFromIE801(dataRequest(
          FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(movementGuarantee = maxGetMovementResponse.movementGuarantee.copy(guarantorTrader = None))
        )) mustBe None
      }
    }
  }
}
