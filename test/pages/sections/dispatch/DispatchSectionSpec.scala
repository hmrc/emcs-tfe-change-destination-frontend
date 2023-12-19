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

package pages.sections.dispatch

import base.SpecBase
import models.requests.DataRequest
import play.api.test.FakeRequest

class DispatchSectionSpec extends SpecBase {
  "isCompleted" - {
    "must return true" - {
      "when Consignor details question is 'yes' and Consignor section is completed" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(DispatchWarehouseExcisePage, "beans")
          .set(DispatchUseConsignorDetailsPage, true)
        )
        DispatchSection.isCompleted mustBe true
      }
      "when Consignor details question is 'no' and the rest of the flow is completed" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(DispatchWarehouseExcisePage, "beans")
          .set(DispatchUseConsignorDetailsPage, false)
          .set(DispatchBusinessNamePage, "beans")
          .set(DispatchAddressPage, testUserAddress)
        )
        DispatchSection.isCompleted mustBe true
      }
    }

    "must return false" - {
      "when not started" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)
        DispatchSection.isCompleted mustBe false
      }
      "when only DispatchWarehouseExcisePage is completed" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(DispatchWarehouseExcisePage, "beans")
        )
        DispatchSection.isCompleted mustBe false
      }
      "when Consignor details question is 'no' and the rest of the flow is not completed" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(DispatchWarehouseExcisePage, "beans")
          .set(DispatchUseConsignorDetailsPage, false)
          .set(DispatchBusinessNamePage, "beans")
        )
        DispatchSection.isCompleted mustBe false
      }
    }
  }
}
