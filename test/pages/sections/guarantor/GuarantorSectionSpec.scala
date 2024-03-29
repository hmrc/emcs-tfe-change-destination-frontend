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

package pages.sections.guarantor

import base.SpecBase
import models.UserAddress
import models.requests.DataRequest
import models.response.emcsTfe.GuarantorType.NoGuarantor
import models.response.emcsTfe.MovementGuaranteeModel
import models.sections.guarantor.GuarantorArranger.{Consignee, Consignor, GoodsOwner, Transporter}
import play.api.test.FakeRequest

class GuarantorSectionSpec extends SpecBase {
  "isCompleted" - {
    "must return true" - {
      "when Consignor is selected" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(GuarantorArrangerPage, Consignor)
        )
        GuarantorSection.isCompleted mustBe true
      }
      "when Consignee is selected" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(GuarantorArrangerPage, Consignee)
        )
        GuarantorSection.isCompleted mustBe true
      }
      Seq(GoodsOwner, Transporter) foreach {
        arranger =>
          s"when another option is selected and the rest of the Guarantor section is completed - ${arranger.getClass.getSimpleName.stripSuffix("$")}" in {
            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
              emptyUserAnswers
                .set(GuarantorArrangerPage, arranger)
                .set(GuarantorNamePage, "")
                .set(GuarantorVatPage, "")
                .set(GuarantorAddressPage, UserAddress(None, "", "", ""))
            )
            GuarantorSection.isCompleted mustBe true
          }
      }
    }

    "must return false" - {
      "when guarantor no answers exist" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers,
          movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(NoGuarantor, None))
        )
        GuarantorSection.isCompleted mustBe false
      }
      Seq(GoodsOwner, Transporter) foreach {
        arranger =>
          s"when another option is selected and the rest of the Guarantor section is not completed - ${arranger.getClass.getSimpleName.stripSuffix("$")}" in {
            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
              emptyUserAnswers
                .set(GuarantorArrangerPage, arranger)
                .set(GuarantorNamePage, "")
                .set(GuarantorVatPage, ""),
              movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(NoGuarantor, None))
            )
            GuarantorSection.isCompleted mustBe false
          }
      }
    }
  }
}
