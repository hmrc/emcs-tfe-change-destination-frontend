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
import models.sections.info.movementScenario.MovementScenario
import models.sections.info.movementScenario.MovementScenario.{ExportWithCustomsDeclarationLodgedInTheEu, ExportWithCustomsDeclarationLodgedInTheUk}
import play.api.test.FakeRequest

class DestinationTypePageSpec extends SpecBase {

  "isExport" - {

    case class Fixture(scenario: MovementScenario) {
      implicit val request: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.set(DestinationTypePage, scenario))
    }

    val exportValues = Seq(ExportWithCustomsDeclarationLodgedInTheUk, ExportWithCustomsDeclarationLodgedInTheEu)

    "must return true" - {
      exportValues.map(scenario =>
        s"when MovementScenario is $scenario" in new Fixture(scenario) {
          DestinationTypePage.isExport mustBe true
        }
      )
    }

    "must return false" - {
      MovementScenario.values.filterNot(exportValues.contains)
        .map(scenario =>
          s"when MovementScenario is $scenario" in new Fixture(scenario) {
            DestinationTypePage.isExport mustBe false
          }
        )
    }
  }
}
