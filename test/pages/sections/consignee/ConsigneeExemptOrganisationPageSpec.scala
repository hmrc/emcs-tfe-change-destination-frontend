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

package pages.sections.consignee

import base.SpecBase
import models.sections.consignee.ExemptOrganisationDetailsModel
import play.api.test.FakeRequest

class ConsigneeExemptOrganisationPageSpec extends SpecBase {

  "getValueFromIE801" - {
    "must return Some(_)" - {
      "when Consignee exists and has ExemptOrganisationDetails" in {
        ConsigneeExemptOrganisationPage.getValueFromIE801(dataRequest(FakeRequest())) mustBe Some(ExemptOrganisationDetailsModel(
          memberState = getMovementResponseModel.memberStateCode.get,
          certificateSerialNumber = getMovementResponseModel.serialNumberOfCertificateOfExemption.get
        ))
      }
    }
    "must return None" - {
      "when IE801 has no memberStateCode" in {
        ConsigneeExemptOrganisationPage.getValueFromIE801(dataRequest(
          FakeRequest(),
          movementDetails = getMovementResponseModel.copy(memberStateCode = None)
        )) mustBe None
      }
      "when IE801 has no serialNumberOfCertificateOfExemption" in {
        ConsigneeExemptOrganisationPage.getValueFromIE801(dataRequest(
          FakeRequest(),
          movementDetails = getMovementResponseModel.copy(serialNumberOfCertificateOfExemption = None)
        )) mustBe None
      }
    }
  }
}
