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

package pages.sections.exportInformation

import base.SpecBase
import play.api.test.FakeRequest

class ExportCustomsOfficePageSpec extends SpecBase {

  val deliveryPlaceCustomsOfficeReference: String = maxGetMovementResponse.deliveryPlaceCustomsOfficeReferenceNumber.get

  "getValueFromIE801" - {
    "must return Some(_)" - {
      "when delivery place customs office reference is defined" in {
        ExportCustomsOfficePage.getValueFromIE801(dataRequest(FakeRequest())) mustBe Some(deliveryPlaceCustomsOfficeReference)
      }
    }
    "must return None" - {
      "when delivery place customs office reference doesn't exist" in {
        ExportCustomsOfficePage.getValueFromIE801(dataRequest(
          FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(deliveryPlaceCustomsOfficeReferenceNumber = None)
        )) mustBe None
      }
    }
  }

}
