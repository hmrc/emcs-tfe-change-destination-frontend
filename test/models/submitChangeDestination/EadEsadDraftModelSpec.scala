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
import models.sections.info.DispatchDetailsModel
import models.sections.info.movementScenario.{MovementScenario, OriginType}
import pages.sections.info.{DestinationTypePage, DispatchDetailsPage}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

import java.time.{LocalDate, LocalTime}

class EadEsadDraftModelSpec extends SpecBase {

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  "apply" - {
    "must return a EadEsadDraftModel with SAD" - {
      "when __RC" in {
        Seq("GBRC123", "XIRC123").foreach {
          ern =>
            implicit val dr: DataRequest[_] = dataRequest(
              request = fakeRequest,
              answers = emptyUserAnswers
                .set(DispatchDetailsPage(), DispatchDetailsModel(LocalDate.parse("2020-10-31"), LocalTime.parse("23:59:59")))
                .set(DestinationTypePage, MovementScenario.ExemptedOrganisation)
              ,
              ern = ern
            )

            EadEsadDraftModel.apply mustBe EadEsadDraftModel(
              originTypeCode = OriginType.Imports,
              dateOfDispatch = "2020-10-31",
              timeOfDispatch = Some("23:59:59")
            )
        }
      }
    }

    "must return a EadEsadDraftModel without SAD" - {
      "when __WK" in {
        Seq("GBWK123", "XIWK123").foreach {
          ern =>
            implicit val dr: DataRequest[_] = dataRequest(
              request = fakeRequest,
              answers = emptyUserAnswers
                .set(DispatchDetailsPage(), DispatchDetailsModel(LocalDate.parse("2020-10-31"), LocalTime.parse("23:59:59")))
                .set(DestinationTypePage, MovementScenario.ExemptedOrganisation)
              ,
              ern = ern
            )

            EadEsadDraftModel.apply mustBe EadEsadDraftModel(
              originTypeCode = OriginType.TaxWarehouse,
              dateOfDispatch = "2020-10-31",
              timeOfDispatch = Some("23:59:59")
            )
        }
      }
    }

    "must add seconds when no seconds are provided in time" in {
      implicit val dr: DataRequest[_] = dataRequest(
        request = fakeRequest,
        answers = emptyUserAnswers
          .set(DispatchDetailsPage(), DispatchDetailsModel(LocalDate.parse("2020-10-31"), LocalTime.parse("23:59")))
          .set(DestinationTypePage, MovementScenario.ExemptedOrganisation)
      )

      EadEsadDraftModel.apply.timeOfDispatch mustBe Some("23:59:00")
    }
  }
}
