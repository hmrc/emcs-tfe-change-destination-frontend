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
import models.sections.guarantor.GuarantorArranger
import pages.sections.guarantor._
import play.api.test.FakeRequest

class MovementGuaranteeModelSpec extends SpecBase {
  "apply" - {
    "must return a MovementGuaranteeModel" - {
      "when no guarantor is required" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(GuarantorRequiredPage, false)
            .set(GuarantorArrangerPage, GuarantorArranger.GoodsOwner)
            .set(GuarantorNamePage, "name")
            .set(GuarantorAddressPage, testUserAddress)
            .set(GuarantorVatPage, "vat")
        )

        MovementGuaranteeModel.apply mustBe MovementGuaranteeModel(GuarantorArranger.NoGuarantorRequired, None)
      }
      "when guarantor is required" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(GuarantorRequiredPage, true)
            .set(GuarantorArrangerPage, GuarantorArranger.GoodsOwner)
            .set(GuarantorNamePage, "name")
            .set(GuarantorAddressPage, testUserAddress)
            .set(GuarantorVatPage, "vat")
        )

        MovementGuaranteeModel.apply mustBe MovementGuaranteeModel(GuarantorArranger.GoodsOwner, Some(Seq(TraderModel(
          None,
          Some("name"),
          Some(AddressModel.fromUserAddress(testUserAddress)),
          Some("vat"),
          None
        ))))
      }
    }
  }
}
