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

package models.requests

import base.SpecBase
import models._
import play.api.test.FakeRequest

class UserRequestSpec extends SpecBase {
  "userTypeFromErn" - {
    Seq(
      ("GBRC123456789", GreatBritainRegisteredConsignor),
      ("XIRC123456789", NorthernIrelandRegisteredConsignor),
      ("GBWK123456789", GreatBritainWarehouseKeeper),
      ("XIWK123456789", NorthernIrelandWarehouseKeeper),
      ("XI00123456789", NorthernIrelandWarehouse),
      ("GB00123456789", GreatBritainWarehouse),
      ("XI11123456789", Unknown)
    ).foreach {
      case (ern, userType) =>
        s"when provided ERN $ern" - {
          s"must return $userType" in {
            val fakeRequest = FakeRequest()
            val request = userRequest(fakeRequest).copy(ern = ern)

            request.userTypeFromErn mustBe userType
          }
        }
    }
  }
}