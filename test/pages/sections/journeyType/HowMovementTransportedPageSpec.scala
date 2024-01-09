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

package pages.sections.journeyType

import base.SpecBase
import models.sections.journeyType.HowMovementTransported.SeaTransport
import play.api.test.FakeRequest

class HowMovementTransportedPageSpec extends SpecBase {

  val transportModeCode: String = maxGetMovementResponse.transportMode.transportModeCode

  "getValueFromIE801" - {
    "must return Some(_)" - {
      "when transport mode code is defined for the journey type" in {
        HowMovementTransportedPage.getValueFromIE801(dataRequest(FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(transportMode = maxGetMovementResponse.transportMode.copy(transportModeCode = "1")))) mustBe Some(SeaTransport)
      }
    }
    "must return None" - {
      "when the transport mode code is invalid" in {
        HowMovementTransportedPage.getValueFromIE801(dataRequest(FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(transportMode = maxGetMovementResponse.transportMode.copy(transportModeCode = "fake")))) mustBe None
      }
    }
  }
}
