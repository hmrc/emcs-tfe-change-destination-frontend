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
import models.sections.ReviewAnswer.KeepAnswers
import models.sections.journeyType.JourneyTime.{Days, Hours}
import pages.sections.journeyType.{JourneyTimeDaysPage, JourneyTimeHoursPage, JourneyTypeReviewPage}
import pages.sections.movement.MovementReviewAnswersPage
import pages.sections.transportArranger.TransportArrangerReviewPage
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class UpdateEadEsadModelSpec extends SpecBase with SubmitChangeDestinationFixtures {

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  "journeyTimeValue" - {

    "must return Some(Hours) when hours" in {

      implicit val dr: DataRequest[_] = dataRequest(
        request = fakeRequest,
        answers = emptyUserAnswers.set(JourneyTimeHoursPage, 1)
      )

      UpdateEadEsadModel.journeyTimeValue mustBe Some(Hours("1"))
    }

    "must return Some(Days) when days" in {

      implicit val dr: DataRequest[_] = dataRequest(
        request = fakeRequest,
        answers = emptyUserAnswers.set(JourneyTimeDaysPage, 1)
      )

      UpdateEadEsadModel.journeyTimeValue mustBe Some(Days("1"))
    }

    "must return None when not supplied" in {

      implicit val dr: DataRequest[_] = dataRequest(
        request = fakeRequest,
        answers = emptyUserAnswers
      )

      UpdateEadEsadModel.journeyTimeValue mustBe None
    }
  }

  "apply" - {

    "must return a model" - {

      "when JourneyType, TransportArranger and Invoice details have all changed" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseFullUserAnswers
        )

        UpdateEadEsadModel.apply mustBe maxUpdateEadEsad
      }

      "when JourneyType, TransportArranger and Invoice details have NOT changed" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseFullUserAnswers
            .set(JourneyTypeReviewPage, KeepAnswers)
            .set(TransportArrangerReviewPage, KeepAnswers)
            .set(MovementReviewAnswersPage, KeepAnswers)
        )

        UpdateEadEsadModel.apply mustBe minUpdateEadEsad
      }
    }
  }
}
