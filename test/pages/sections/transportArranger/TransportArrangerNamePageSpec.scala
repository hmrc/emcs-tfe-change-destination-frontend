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

package pages.sections.transportArranger

import base.SpecBase
import models.response.emcsTfe.TraderModel
import play.api.test.FakeRequest

class TransportArrangerNamePageSpec extends SpecBase {

  val transportArrangerTrader: TraderModel = maxGetMovementResponse.transportArrangerTrader.get

  "getValueFromIE801" - {
    "must return Some(_)" - {
      "when transport arranger trader is defined and has a trader name" in {
        TransportArrangerNamePage.getValueFromIE801(dataRequest(FakeRequest())) mustBe transportArrangerTrader.traderName
      }
    }

    "must return None" - {
      "when transport arranger trader and has no trader name" in {
        TransportArrangerNamePage.getValueFromIE801(dataRequest(
          FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(transportArrangerTrader = maxGetMovementResponse.transportArrangerTrader.map(_.copy(traderName = None)))
        )) mustBe None
      }
      
      "when transport arranger trader trader doesn't exist" in {
        TransportArrangerNamePage.getValueFromIE801(dataRequest(
          FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(transportArrangerTrader = None)
        )) mustBe None
      }
    }
  }
}
