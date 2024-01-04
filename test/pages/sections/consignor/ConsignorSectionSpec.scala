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

package pages.sections.consignor

import base.SpecBase
import models.requests.DataRequest
import models.sections.ReviewAnswer.{ChangeAnswers, KeepAnswers}
import play.api.test.FakeRequest

class ConsignorSectionSpec extends SpecBase {

  "isCompleted" - {
    "should return true" - {
      "when keep answers has been selected" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(),
            emptyUserAnswers
              .set(ConsignorReviewPage, KeepAnswers)
          )
        ConsignorSection.isCompleted mustBe true
      }

      "an answer for ConsignorAddressPage has been provided" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(),
            emptyUserAnswers
              .set(ConsignorAddressPage, testUserAddress)
              .set(ConsignorReviewPage, ChangeAnswers)
          )
        ConsignorSection.isCompleted mustBe true
      }
    }

    "should return false" - {
      "when no answer for ConsignorAddressPage has been provided" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(),
            emptyUserAnswers
              .set(ConsignorReviewPage, ChangeAnswers)
          )
        ConsignorSection.isCompleted mustBe false
      }
    }
  }

}
