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

package models.submitChangeDestination

import base.SpecBase

class AddressModelSpec extends SpecBase {
  "fromUserAddress" - {
    "must turn a UserAddress into an AddressModel" in {
      AddressModel.fromUserAddress(testUserAddress) mustBe AddressModel(
        streetNumber = testUserAddress.property,
        street = Some(testUserAddress.street),
        postcode = Some(testUserAddress.postcode),
        city = Some(testUserAddress.town)
      )
      AddressModel.fromUserAddress(testUserAddress.copy(property = None)) mustBe AddressModel(
        streetNumber = None,
        street = Some(testUserAddress.street),
        postcode = Some(testUserAddress.postcode),
        city = Some(testUserAddress.town)
      )
    }
  }
}
