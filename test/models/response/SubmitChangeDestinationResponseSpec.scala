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

package models.response

import base.SpecBase
import fixtures.SubmitChangeDestinationFixtures

class SubmitChangeDestinationResponseSpec extends SpecBase with SubmitChangeDestinationFixtures {
  "reads" - {
    "should read from EIS" in {
      successResponseEISJson.as[SubmitChangeDestinationResponse] mustBe submitChangeDestinationResponseEIS
    }
    "should read from ChRIS" in {
      successResponseChRISJson.as[SubmitChangeDestinationResponse] mustBe submitChangeDestinationResponseChRIS
    }
  }
}
