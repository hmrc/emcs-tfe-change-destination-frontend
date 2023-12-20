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
import models.sections.info.movementScenario.DestinationType
import pages.sections.info.DeferredMovementPage
import play.api.test.FakeRequest

class AttributesModelSpec extends SpecBase {
  "apply" - {
    "must return an AttributesModel" - {
      "when XIP ERN" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.set(DeferredMovementPage(), true), "XIPC123")

        AttributesModel.apply(DestinationType.Export) mustBe AttributesModel(SubmissionMessageType.DutyPaidB2B, Some(true))
      }
      "when not XIP ERN, but Export" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.set(DeferredMovementPage(), false))

        AttributesModel.apply(DestinationType.Export) mustBe AttributesModel(SubmissionMessageType.Export, Some(false))
      }
      "when not XIP ERN and not Export" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest())

        AttributesModel.apply(DestinationType.TaxWarehouse) mustBe AttributesModel(SubmissionMessageType.Standard, None)
      }
    }
  }
}
