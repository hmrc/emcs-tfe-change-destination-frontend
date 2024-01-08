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

package pages.sections.transportUnit

import base.SpecBase
import models.Index
import models.response.emcsTfe.TransportDetailsModel
import play.api.libs.json.Json
import play.api.test.FakeRequest

class TransportUnitGiveMoreInformationPageSpec extends SpecBase {

  val transportDetails: TransportDetailsModel = maxGetMovementResponse.transportDetails.head

  "getValueFromIE801" - {
    "must return Some(_)" - {
      "when the index is valid" in {
        TransportUnitGiveMoreInformationPage(testIndex1).getValueFromIE801(dataRequest(FakeRequest(), emptyUserAnswers.set(
          TransportUnitsSectionUnits, Json.arr(Json.obj())
        ))) mustBe Some(transportDetails.complementaryInformation)
      }
    }

    "must return None" - {

      //scalastyle:off
      "when the index exceeds the transport units in 801" in {
        TransportUnitGiveMoreInformationPage(Index(3)).getValueFromIE801(dataRequest(FakeRequest(), emptyUserAnswers.set(
          TransportUnitsSectionUnits, Json.arr(Json.obj(), Json.obj(), Json.obj(), Json.obj())
        ))) mustBe None
      }

      "when there are no transport units" in {
        TransportUnitGiveMoreInformationPage(testIndex1).getValueFromIE801(dataRequest(
          FakeRequest(),
          movementDetails = maxGetMovementResponse.copy(transportDetails = Seq.empty)
        )) mustBe None
      }
    }
  }

}
