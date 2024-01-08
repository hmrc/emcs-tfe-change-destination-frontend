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
import play.api.libs.json.JsResultException
import play.api.test.FakeRequest

class JourneyTimeDaysPageSpec extends SpecBase {

  "getValueFromIE801" - {
    "must return Some(_)" - {
      "when journey time is in days" in {
        JourneyTimeDaysPage.getValueFromIE801(dataRequest(FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(journeyTime = "1 days"))) mustBe Some(1)
      }
    }

    "must return None" - {
      "when the journey time is in hours" in {
        JourneyTimeDaysPage.getValueFromIE801(dataRequest(FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(journeyTime = "1 hours"))) mustBe None
      }
    }

    "must throw an exception" - {
      "when the journey time is invalid" in {
        intercept[JsResultException](JourneyTimeDaysPage.getValueFromIE801(dataRequest(FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(journeyTime = "fake")))).getMessage mustBe "JsResultException(errors:List((,List(JsonValidationError(List(Could not parse JourneyTime from JSON, received: 'fake'),ArraySeq())))))"
      }
    }
  }
}
