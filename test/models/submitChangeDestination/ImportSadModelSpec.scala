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

package models.submitChangeDestination

import base.SpecBase
import models.requests.DataRequest
import models.response.MissingMandatoryPage
import pages.sections.sad.ImportNumberPage
import play.api.test.FakeRequest

class ImportSadModelSpec extends SpecBase {
  "apply" - {
    "must throw an error" - {
      "when no SAD" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
        )

        val result = intercept[MissingMandatoryPage](ImportSadModel.apply)

        result.message mustBe "SadSection should contain at least one item"
      }
    }

    "must return a Seq(ImportSadModel)" - {
      "when one SAD" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(ImportNumberPage(testIndex1), "sad 1")
        )

        ImportSadModel.apply mustBe Seq(ImportSadModel("sad 1"))
      }
      "when more than one SAD" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(ImportNumberPage(testIndex1), "sad 1")
            .set(ImportNumberPage(testIndex2), "sad 2")
            .set(ImportNumberPage(testIndex3), "sad 3")
        )

        ImportSadModel.apply mustBe Seq(ImportSadModel("sad 1"), ImportSadModel("sad 2"), ImportSadModel("sad 3"))
      }
    }
  }
}
