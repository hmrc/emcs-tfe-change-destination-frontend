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

package pages.sections.firstTransporter

import base.SpecBase
import models.response.emcsTfe.TraderModel
import play.api.test.FakeRequest

class FirstTransporterVatPageSpec extends SpecBase {

  val firstTransporterTrader: TraderModel = maxGetMovementResponse.firstTransporterTrader.get

  "getValueFromIE801" - {
    "must return Some(_)" - {
      "when first transporter trader is defined and has a VAT number" in {
        FirstTransporterVatPage.getValueFromIE801(dataRequest(FakeRequest())) mustBe firstTransporterTrader.vatNumber
      }
    }
    "must return None" - {
      "when first transporter trader and has no vat number" in {
        FirstTransporterVatPage.getValueFromIE801(dataRequest(
          FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(firstTransporterTrader = Some(firstTransporterTrader.copy(vatNumber = None)))
        )) mustBe None
      }
      "when first transporter trader doesn't exist" in {
        FirstTransporterVatPage.getValueFromIE801(dataRequest(
          FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(firstTransporterTrader = None)
        )) mustBe None
      }
    }
  }
}
