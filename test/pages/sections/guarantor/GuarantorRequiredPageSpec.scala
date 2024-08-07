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

package pages.sections.guarantor

import base.SpecBase
import models.response.emcsTfe.{GuarantorType, MovementGuaranteeModel}
import play.api.test.FakeRequest

class GuarantorRequiredPageSpec extends SpecBase {

  "getValueFromIE801" - {

    GuarantorType.values.filterNot(GuarantorType.noGuarantorValues.contains).foreach { guarantorType =>

      s"when GuarantorType is $guarantorType" - {
        "must return Some(true)" in {
          GuarantorRequiredPage.getValueFromIE801(dataRequest(
            FakeRequest(),
            ern = testGreatBritainWarehouseErn,
            movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(guarantorType, None))
          )) mustBe Some(true)
        }
      }
    }

    GuarantorType.noGuarantorValues.foreach { guarantorType =>

      s"when GuarantorType is $guarantorType" - {
        "must return Some(false)" in {
          GuarantorRequiredPage.getValueFromIE801(dataRequest(
            FakeRequest(),
            ern = testGreatBritainWarehouseErn,
            movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(guarantorType, None))
          )) mustBe Some(false)
        }
      }
    }
  }
}
