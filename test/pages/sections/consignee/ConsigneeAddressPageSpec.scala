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

package pages.sections.consignee

import base.SpecBase
import models.UserAddress
import models.response.emcsTfe.AddressModel
import play.api.test.FakeRequest

class ConsigneeAddressPageSpec extends SpecBase {
  "getValueFromIE801" - {
    "must return Some(_)" - {
      "when Consignee exists and has an address" in {
        val consigneeAddress: AddressModel = maxGetMovementResponse.consigneeTrader.get.address.get
        ConsigneeAddressPage.getValueFromIE801(dataRequest(FakeRequest())) mustBe Some(UserAddress(
          property = Some(consigneeAddress.streetNumber.get),
          street = consigneeAddress.street.get,
          town = consigneeAddress.city.get,
          postcode = consigneeAddress.postcode.get
        ))
      }
    }
    "must return None" - {
      "when Consignee exists and has no address" in {
        ConsigneeAddressPage.getValueFromIE801(dataRequest(
          FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(consigneeTrader = Some(maxGetMovementResponse.consigneeTrader.get.copy(address = None)))
        )) mustBe None
      }
      "when Consignee doesn't exist" in {
        ConsigneeAddressPage.getValueFromIE801(dataRequest(
          FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(consigneeTrader = None)
        )) mustBe None
      }
    }
  }
}
