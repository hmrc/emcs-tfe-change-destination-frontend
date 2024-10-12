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
import models.response.emcsTfe.TraderModel
import play.api.test.FakeRequest

class ConsigneeAddressPageSpec extends SpecBase {
  "getValueFromIE801" - {
    "must return Some(_)" - {
      "when Consignee exists in the movement" in {
        val consigneeTrader: TraderModel = maxGetMovementResponse.consigneeTrader.get
        ConsigneeAddressPage.getValueFromIE801(dataRequest(FakeRequest())) mustBe Some(UserAddress(
          businessName = consigneeTrader.traderName,
          property = consigneeTrader.address.flatMap(_.streetNumber),
          street = consigneeTrader.address.flatMap(_.street).getOrElse(""),
          town = consigneeTrader.address.flatMap(_.city).getOrElse(""),
          postcode = consigneeTrader.address.flatMap(_.postcode).getOrElse("")
        ))
      }
    }
    "must return None" - {
      "when Consignee doesn't exist within the movement" in {
        ConsigneeAddressPage.getValueFromIE801(dataRequest(
          FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(consigneeTrader = None)
        )) mustBe None
      }
    }
  }
}
