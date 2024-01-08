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
import models.ExemptOrganisationDetailsModel
import models.requests.DataRequest
import pages.sections.consignee.ConsigneeExemptOrganisationPage
import play.api.test.FakeRequest

class ComplementConsigneeTraderModelSpec extends SpecBase {
  "apply" - {
    "must return Some(ComplementConsigneeTraderModel)" - {
      "when ConsigneeExemptOrganisationPage has an answer" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers.set(ConsigneeExemptOrganisationPage, ExemptOrganisationDetailsModel("state", "number"))
        )

        ComplementConsigneeTraderModel.apply mustBe Some(ComplementConsigneeTraderModel("state", Some("number")))
      }
    }
    "must return None" - {
      "when ConsigneeExemptOrganisationPage has no answer" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers,
          movementDetails = maxGetMovementResponse.copy(memberStateCode = None, serialNumberOfCertificateOfExemption = None)
        )

        ComplementConsigneeTraderModel.apply mustBe None
      }
    }
  }
}
