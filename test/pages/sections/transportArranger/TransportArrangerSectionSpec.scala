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

package pages.sections.transportArranger

import base.SpecBase
import models.UserAddress
import models.requests.DataRequest
import models.sections.ReviewAnswer.{ChangeAnswers, KeepAnswers}
import models.sections.transportArranger.TransportArranger._
import play.api.test.FakeRequest

class TransportArrangerSectionSpec extends SpecBase {
  "isCompleted" - {
    "must return true" - {
      "when Consignor is selected" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(TransportArrangerPage, Consignor)
            .set(TransportArrangerReviewPage, ChangeAnswers)
        )
        TransportArrangerSection.isCompleted mustBe true
      }
      "when Consignee is selected" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(TransportArrangerPage, Consignee)
            .set(TransportArrangerReviewPage, ChangeAnswers)
        )
        TransportArrangerSection.isCompleted mustBe true
      }
      Seq(GoodsOwner, Other).foreach {
        arranger =>
          s"when ${arranger.getClass.getSimpleName.stripSuffix("$")} is selected and the rest of the TransportArranger section is completed" in {
            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
              emptyUserAnswers
                .set(TransportArrangerPage, arranger)
                .set(TransportArrangerNamePage, "")
                .set(TransportArrangerVatPage, "")
                .set(TransportArrangerAddressPage, UserAddress(None, "", "", ""))
                .set(TransportArrangerReviewPage, ChangeAnswers)
            )
            TransportArrangerSection.isCompleted mustBe true
          }
      }

      "keep answers has been selected" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(),
            emptyUserAnswers
              .set(TransportArrangerReviewPage, KeepAnswers)
          )
        TransportArrangerSection.isCompleted mustBe true
      }
    }
  }
}
