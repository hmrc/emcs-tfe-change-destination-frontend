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

package pages.sections.documents

import base.SpecBase
import fixtures.DocumentTypeFixtures
import models.requests.DataRequest
import play.api.test.FakeRequest
import viewmodels.taskList.{Completed, InProgress, NotStarted}

class DocumentsSectionUnitsSpec extends SpecBase with DocumentTypeFixtures {

  "status" - {

    "return Completed" - {

      "when there is ONE document added and Completed" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(DocumentTypePage(0), documentTypeModel)
          .set(DocumentReferencePage(0), "reference")
        )

        DocumentsSectionUnits.status mustBe Completed
      }

      "when there are TWO documents added both Completed" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(DocumentTypePage(0), documentTypeModel)
          .set(DocumentReferencePage(0), "reference")
          .set(DocumentTypePage(1), documentTypeOtherModel)
          .set(ReferenceAvailablePage(1), false)
          .set(DocumentDescriptionPage(1), "description")
        )

        DocumentsSectionUnits.status mustBe Completed
      }
    }

    "return InProgress" - {

      "when there is ONE documents added but InProgress" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ReferenceAvailablePage(0), true)
        )

        DocumentsSectionUnits.status mustBe InProgress
      }

      "when there are TWO documents added, one in Completed and one InProgress" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(DocumentTypePage(0), documentTypeOtherModel)
          .set(ReferenceAvailablePage(0), true)
          .set(DocumentReferencePage(0), "reference")
          .set(DocumentTypePage(1), documentTypeOtherModel)
        )

        DocumentsSectionUnits.status mustBe InProgress
      }
    }

    "return NotStarted" - {

      "when there are NO documents added" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)

        DocumentsSectionUnits.status mustBe NotStarted
      }
    }
  }
}
