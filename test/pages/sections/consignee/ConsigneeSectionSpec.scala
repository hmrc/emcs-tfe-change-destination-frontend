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
import models.requests.DataRequest
import models.sections.consignee.{ConsigneeExportVat, ConsigneeExportVatType}
import models.{ExemptOrganisationDetailsModel, UserAddress}
import play.api.test.FakeRequest

class ConsigneeSectionSpec extends SpecBase {
  "isCompleted" - {
    "must return true" - {
      "when user starts on ConsigneeExportUkEu and selects yes" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExportPage, true)
            .set(ConsigneeExportVatPage, ConsigneeExportVat(ConsigneeExportVatType.No, None, None))
            .set(ConsigneeBusinessNamePage, "")
            .set(ConsigneeAddressPage, UserAddress(None, "", "", ""))
        )
        ConsigneeSection.isCompleted mustBe true
      }
      "when user starts on ConsigneeExportUkEu and selects no" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExportPage, false)
            .set(ConsigneeExcisePage, "")
            .set(ConsigneeBusinessNamePage, "")
            .set(ConsigneeAddressPage, UserAddress(None, "", "", ""))
        )
        ConsigneeSection.isCompleted mustBe true
      }
      "when user starts on ConsigneeExcise" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExcisePage, "")
            .set(ConsigneeBusinessNamePage, "")
            .set(ConsigneeAddressPage, UserAddress(None, "", "", ""))
        )
        ConsigneeSection.isCompleted mustBe true
      }
      "when user starts on ConsigneeExemptOrganisation" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExemptOrganisationPage, ExemptOrganisationDetailsModel("", ""))
            .set(ConsigneeBusinessNamePage, "")
            .set(ConsigneeAddressPage, UserAddress(None, "", "", ""))
        )
        ConsigneeSection.isCompleted mustBe true
      }
    }

    "must return false" - {
      "when user answers doesn't contain ConsigneeExportUkEu, ConsigneeExcise or ConsigneeExemptOrganisation" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)
        ConsigneeSection.isCompleted mustBe false
      }
      "when user starts on ConsigneeExportUkEu, answers that page and doesn't finish the flow" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.set(ConsigneeExportPage, true))
        ConsigneeSection.isCompleted mustBe false
      }
      "when user starts on ConsigneeExcise, answers that page and doesn't finish the flow" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.set(ConsigneeExcisePage, ""))
        ConsigneeSection.isCompleted mustBe false
      }
      "when user starts on ConsigneeExemptOrganisation, answers that page and doesn't finish the flow" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.set(ConsigneeExemptOrganisationPage, ExemptOrganisationDetailsModel("", "")))
        ConsigneeSection.isCompleted mustBe false
      }
    }
  }
}
