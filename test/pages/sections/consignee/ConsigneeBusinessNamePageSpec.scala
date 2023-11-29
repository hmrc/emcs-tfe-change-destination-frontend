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
import models.response.emcsTfe.TraderModel
import play.api.test.FakeRequest

class ConsigneeBusinessNamePageSpec extends SpecBase {

  val consignee: TraderModel = getMovementResponseModel.consigneeTrader.get

  "getValueFromIE801" - {
    "must return Some(_)" - {
      "when Consignee exists and has a name" in {
        ConsigneeBusinessNamePage.getValueFromIE801(dataRequest(FakeRequest())) mustBe Some(consignee.traderName.get)
      }
    }
    "must return None" - {
      "when Consignee exists and has no address" in {
        ConsigneeBusinessNamePage.getValueFromIE801(dataRequest(
          FakeRequest(),
          movementDetails = getMovementResponseModel.copy(consigneeTrader = Some(consignee.copy(traderName = None)))
        )) mustBe None
      }
      "when Consignee doesn't exist" in {
        ConsigneeBusinessNamePage.getValueFromIE801(dataRequest(
          FakeRequest(),
          movementDetails = getMovementResponseModel.copy(consigneeTrader = None)
        )) mustBe None
      }
    }
  }
}
