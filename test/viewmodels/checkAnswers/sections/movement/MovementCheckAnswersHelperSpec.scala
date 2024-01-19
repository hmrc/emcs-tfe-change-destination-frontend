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

package viewmodels.checkAnswers.sections.movement

import base.SpecBase
import models.requests.DataRequest
import play.api.test.FakeRequest

class MovementCheckAnswersHelperSpec extends SpecBase {

  lazy val request: FakeRequest[_] = FakeRequest()
  lazy val helper = new MovementCheckAnswersHelper()

  "summaryList" - {

    Seq(true, false).foreach { onReviewPage =>
      s"when onReviewPage = $onReviewPage" - {

        "return 2 rows" - {

          "when there is an answer for InvoiceDetailsPage" in {
            helper.summaryList(onReviewPage)(dataRequest(request), messages(request)).rows.length mustBe 2
          }
        }

        "return no rows" - {

          "when there is not an answer for InvoiceDetails" in {
            implicit val dr: DataRequest[_] = dataRequest(request,
              movementDetails = maxGetMovementResponse.copy(eadEsad = maxGetMovementResponse.eadEsad.copy(invoiceDate = None)))
            helper.summaryList(onReviewPage)(dr, messages(request)).rows.length mustBe 0
          }
        }
      }
    }
  }
}
