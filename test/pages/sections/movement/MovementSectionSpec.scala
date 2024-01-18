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

package pages.sections.movement

import base.SpecBase
import models.requests.DataRequest
import models.sections.ReviewAnswer.{ChangeAnswers, KeepAnswers}
import play.api.test.FakeRequest
import viewmodels.taskList._

class MovementSectionSpec extends SpecBase {

  "status" - {

    "must be Completed" - {

      "when MovementReviewPage is KeepAnswers" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(MovementReviewPage, KeepAnswers)
        )

        MovementSection.status mustBe Completed
      }

      "when MovementReviewPage is ChangeAnswers (all answers complete)" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(MovementReviewPage, ChangeAnswers)
        )

        MovementSection.status mustBe Completed
      }
    }

    "must be InProgress" - {

      "when MovementReviewPage is ChangeAnswers (the answers are not complete)" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(MovementReviewPage, ChangeAnswers),
          movementDetails = maxGetMovementResponse.copy(eadEsad = maxGetMovementResponse.eadEsad.copy(invoiceDate = None))
        )

        MovementSection.status mustBe InProgress
      }
    }

    "must be Review" - {

      "when MovementReviewPage has NOT been answered" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
        )

        MovementSection.status mustBe Review
      }
    }
  }
}
