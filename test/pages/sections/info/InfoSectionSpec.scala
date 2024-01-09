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

package pages.sections.info

import base.SpecBase
import models.requests.DataRequest
import models.sections.info.DispatchPlace.NorthernIreland
import models.sections.info.movementScenario.MovementScenario.GbTaxWarehouse
import play.api.test.FakeRequest

class InfoSectionSpec extends SpecBase {

  "isCompleted" - {
    "must return true" - {
      "when the user is a XIWK and the dispatch place has been provided (all other answers provided)" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(DestinationTypePage, GbTaxWarehouse)
            .set(DispatchDetailsPage(), dispatchDetailsModel)
            .set(DispatchPlacePage, NorthernIreland),
          ern = testNorthernIrelandErn
        )
        InfoSection.isCompleted mustBe true
      }

      "when the user is a GBWK (all other answers provided)" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(DestinationTypePage, GbTaxWarehouse)
            .set(DispatchDetailsPage(), dispatchDetailsModel)
        )
        InfoSection.isCompleted mustBe true
      }
    }

    "must return false" - {
      "when not finished" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(DestinationTypePage, GbTaxWarehouse)
        )
        InfoSection.isCompleted mustBe false
      }
    }
  }

}
