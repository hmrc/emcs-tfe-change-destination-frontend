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
import models.sections.transportArranger.TransportArranger
import pages.sections.journeyType.{JourneyTimeDaysPage, JourneyTimeHoursPage}
import pages.sections.transportArranger.TransportArrangerPage
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class HeaderEadEsadModelSpec extends SpecBase {

  lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  "apply" - {
    "must return a HeaderEadEsadModel" - {
      "when hours" in {
        implicit val dr: DataRequest[_] = dataRequest(
          fakeRequest,
          emptyUserAnswers
            .set(JourneyTimeHoursPage, 2)
            .set(TransportArrangerPage, TransportArranger.GoodsOwner),
          movementDetails = maxGetMovementResponse.copy(journeyTime = "1 hours")
        )

          HeaderEadEsadModel.apply(DestinationType.TemporaryRegisteredConsignee) mustBe HeaderEadEsadModel(
            destinationType = DestinationType.TemporaryRegisteredConsignee,
            journeyTime = "2 hours",
            transportArrangement = TransportArranger.GoodsOwner
          )
      }
      "when days" in {
        implicit val dr: DataRequest[_] = dataRequest(
          fakeRequest,
          emptyUserAnswers
            .set(JourneyTimeDaysPage, 3)
            .set(TransportArrangerPage, TransportArranger.Other),
          movementDetails = maxGetMovementResponse.copy(journeyTime = "1 days")
        )

          HeaderEadEsadModel.apply(DestinationType.TemporaryRegisteredConsignee) mustBe HeaderEadEsadModel(
            destinationType = DestinationType.TemporaryRegisteredConsignee,
            journeyTime = "3 days",
            transportArrangement = TransportArranger.Other
          )
      }
    }
  }
}
