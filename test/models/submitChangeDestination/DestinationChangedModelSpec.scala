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

package models.submitChangeDestination

import base.SpecBase
import fixtures.SubmitChangeDestinationFixtures
import models.requests.DataRequest
import models.response.emcsTfe.GuarantorType.NoGuarantor
import models.sections.ReviewAnswer.KeepAnswers
import models.sections.info.ChangeType.Destination
import models.sections.info.movementScenario.MovementScenario.UkTaxWarehouse
import pages.sections.destination.{DestinationAddressPage, DestinationBusinessNamePage, DestinationConsigneeDetailsPage, DestinationWarehouseExcisePage}
import pages.sections.guarantor.GuarantorReviewPage
import pages.sections.info.{ChangeTypePage, DestinationTypePage}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class DestinationChangedModelSpec extends SpecBase with SubmitChangeDestinationFixtures {

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  "apply" - {

    "must return a model" - {

      "when consignee has changed, destination is now Export and guarantor is required" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseFullUserAnswers,
          ern = testGreatBritainErn,
          movementDetails = maxGetMovementResponse.copy(movementGuarantee = models.response.emcsTfe.MovementGuaranteeModel(NoGuarantor, None))
        )

        DestinationChangedModel.apply mustBe maxDestinationChangedExport
      }

      "when consignee has NOT changed, destinationType is NOT changed and guarantor is NOT required" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseFullUserAnswers
            .remove(DestinationTypePage)
            .set(ChangeTypePage, Destination)
            .set(DestinationWarehouseExcisePage, testGreatBritainErn)
            .set(DestinationConsigneeDetailsPage, false)
            .set(DestinationBusinessNamePage, "destination name")
            .set(DestinationAddressPage, testUserAddress.copy(street = "destination street"))
            .set(DestinationTypePage, UkTaxWarehouse.GB)
            .set(GuarantorReviewPage, KeepAnswers),
          ern = testGreatBritainWarehouseErn
        )

        DestinationChangedModel.apply mustBe minDestinationChanged
      }
    }
  }
}
