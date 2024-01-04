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

package pages.sections.destination

import base.SpecBase
import models.response.emcsTfe.TraderModel
import play.api.test.FakeRequest

class DestinationBusinessNamePageSpec extends SpecBase {
  val deliverPlaceTrader: TraderModel = maxGetMovementResponse.deliveryPlaceTrader.get

  "getValueFromIE801" - {
    "must return Some(_)" - {
      "when deliver place trader exists and has a name" in {
        DestinationBusinessNamePage.getValueFromIE801(dataRequest(FakeRequest())) mustBe deliverPlaceTrader.traderName
      }
    }
    "must return None" - {
      "when deliver place trader exists and has no name" in {
        DestinationBusinessNamePage.getValueFromIE801(dataRequest(
          FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(deliveryPlaceTrader = Some(deliverPlaceTrader.copy(traderName = None)))
        )) mustBe None
      }
      "when delivery place trader doesn't exist" in {
        DestinationBusinessNamePage.getValueFromIE801(dataRequest(
          FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(deliveryPlaceTrader = None)
        )) mustBe None
      }
    }
  }
}
