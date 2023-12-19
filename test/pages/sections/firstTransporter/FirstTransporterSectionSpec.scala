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

package pages.sections.firstTransporter

import base.SpecBase
import models.UserAddress
import models.requests.DataRequest
import play.api.test.FakeRequest

class FirstTransporterSectionSpec extends SpecBase {
  "isCompleted" - {
    "must return true" - {
      "when finished" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(FirstTransporterNamePage, "")
            .set(FirstTransporterVatPage, "")
            .set(FirstTransporterAddressPage, UserAddress(None, "", "", ""))
        )
        FirstTransporterSection.isCompleted mustBe true
      }
    }

    "must return false" - {
      "when not finished" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.set(FirstTransporterNamePage, ""))
        FirstTransporterSection.isCompleted mustBe false
      }
    }

    "must return false" - {
      "when not empty" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)
        FirstTransporterSection.isCompleted mustBe false
      }
    }
  }
}
