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

package pages.sections.transportUnit

import base.SpecBase
import models.Index
import models.requests.DataRequest
import models.sections.transportUnit.TransportSealTypeModel
import models.sections.transportUnit.TransportUnitType.{Container, FixedTransport}
import play.api.test.FakeRequest

class TransportUnitSectionSpec extends SpecBase {
  "isCompleted" - {
    "must return true" - {
      "when mandatory pages are answered, sca = false" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(),
            emptyUserAnswers
              .set(TransportUnitTypePage(testIndex1), Container)
              .set(TransportUnitIdentityPage(testIndex1), "")
              .set(TransportSealChoicePage(testIndex1), false)
          )
        TransportUnitSection(Index(0)).isCompleted mustBe true
      }
      "when mandatory pages are answered, sca = true and seal choice has an answer" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(),
            emptyUserAnswers
              .set(TransportUnitTypePage(testIndex1), Container)
              .set(TransportUnitIdentityPage(testIndex1), "")
              .set(TransportSealChoicePage(testIndex1), true)
              .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("", None))
          )
        TransportUnitSection(Index(0)).isCompleted mustBe true
      }
      "when mandatory pages are answered, sca = true, all pages have an answer" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(),
            emptyUserAnswers
              .set(TransportUnitTypePage(testIndex1), Container)
              .set(TransportUnitIdentityPage(testIndex1), "")
              .set(TransportSealChoicePage(testIndex1), true)
              .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("", None))
              .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)
              .set(TransportUnitGiveMoreInformationPage(testIndex1), Some(""))
          )
        TransportUnitSection(Index(0)).isCompleted mustBe true
      }

      "when transport unit identity is Fixed Transport" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(),
            emptyUserAnswers
              .set(TransportUnitTypePage(testIndex1), FixedTransport)
          )
        TransportUnitSection(Index(0)).isCompleted mustBe true
      }
    }

    "must return false" - {
      "when mandatory screens are missing an answer" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)
        TransportUnitSection(Index(0)).isCompleted mustBe false
      }
    }
  }
}
