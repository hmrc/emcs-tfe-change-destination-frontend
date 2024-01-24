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

package viewmodels.checkAnswers.sections.exportInformation

import base.SpecBase
import pages.sections.exportInformation.ExportCustomsOfficePage
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class ExportInformationCheckAnswersHelperSpec extends SpecBase {

  val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
  val helper = new ExportInformationCheckAnswersHelper

  "ExportInformationCheckAnswersHelper" - {

    "should return no rows" - {

      "when there is no answer for the ExportCustomsOfficePage" in {

        helper.summaryList()(
          dataRequest(request, movementDetails = maxGetMovementResponse.copy(deliveryPlaceCustomsOfficeReferenceNumber = None)),
          messages(request)).rows.isEmpty mustBe true
      }
    }

    "should return 1 row" - {

      "when there is an answer for the ExportCustomsOfficePage" in {

        helper.summaryList()(dataRequest(request, emptyUserAnswers.set(ExportCustomsOfficePage, "GB00056")), messages(request)).rows.size mustBe 1
      }
    }
  }

}
