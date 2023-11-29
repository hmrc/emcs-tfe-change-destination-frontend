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

package models

import base.SpecBase
import fixtures.UserAddressFixtures
import play.api.libs.json.{JsSuccess, Json}

class UserAddressSpec extends SpecBase with UserAddressFixtures {

  "UserAddress with MAX values" - {

    "should read from json" in {
      Json.fromJson[UserAddress](userAddressJsonMax) mustBe JsSuccess(userAddressModelMax)
    }
    "should write to json" in {
      Json.toJson(userAddressModelMax) mustBe userAddressJsonMax
    }
  }

  "UserAddress with MIN values" - {

    "should read from json" in {
      Json.fromJson[UserAddress](userAddressJsonMin) mustBe JsSuccess(userAddressModelMin)
    }
    "should write to json" in {
      Json.toJson(userAddressModelMin) mustBe userAddressJsonMin
    }
  }
}
