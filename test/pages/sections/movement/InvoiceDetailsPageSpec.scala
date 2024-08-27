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

package pages.sections.movement

import base.SpecBase
import models.sections.info.InvoiceDetailsModel
import play.api.test.FakeRequest

import java.time.LocalDate

class InvoiceDetailsPageSpec extends SpecBase {

  "getValueFromIE801" - {
    "must return Some(_)" - {
      "when Invoice Date is defined" in {
        InvoiceDetailsPage.getValueFromIE801(dataRequest(FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(eadEsad = maxGetMovementResponse.eadEsad.copy(invoiceDate = Some("2023-01-01"))))) mustBe Some(InvoiceDetailsModel("EadEsadInvoiceNumber", Some(LocalDate.of(2023, 1, 1))))
      }

      "when Invoice Date is NOT defined" in {
        InvoiceDetailsPage.getValueFromIE801(dataRequest(FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(eadEsad = maxGetMovementResponse.eadEsad.copy(invoiceDate = None)))) mustBe Some(InvoiceDetailsModel("EadEsadInvoiceNumber", None))
      }
    }
  }

}
