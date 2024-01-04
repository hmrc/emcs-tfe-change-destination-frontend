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

package pages.sections.journeyType

import base.SpecBase
import models.requests.DataRequest
import models.sections.ReviewAnswer.{ChangeAnswers, KeepAnswers}
import models.sections.journeyType.HowMovementTransported
import models.sections.journeyType.HowMovementTransported.Other
import play.api.test.FakeRequest

class JourneyTypeSectionSpec extends SpecBase {
  "isCompleted" - {
    "must return true" - {

      "when the journey type section has been marked as 'change answers' but all the pages have been completed" in {
        val completedUserAnswers = emptyUserAnswers
          .set(HowMovementTransportedPage, Other)
          .set(GiveInformationOtherTransportPage, "information")
          .set(JourneyTimeDaysPage, 1)
          .set(JourneyTypeReviewPage, ChangeAnswers)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), completedUserAnswers)
        JourneyTypeSection.isCompleted mustBe true
      }

      "when the journey type section has been reviewed and" - {
        "when finished and HowMovementTransportedPage is Other" in {
          val completedUserAnswers = emptyUserAnswers
            .set(HowMovementTransportedPage, Other)
            .set(GiveInformationOtherTransportPage, "information")
            .set(JourneyTimeDaysPage, 1)
            .set(JourneyTypeReviewPage, ChangeAnswers)

          implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), completedUserAnswers)
          JourneyTypeSection.isCompleted mustBe true
        }

        HowMovementTransported.values.filterNot(_ == Other).foreach(
          answer =>
            s"when finished and HowMovementTransportedPage is ${answer.getClass.getSimpleName.stripSuffix("$")}" in {
              val completedUserAnswers = emptyUserAnswers
                .set(HowMovementTransportedPage, answer)
                .set(JourneyTimeHoursPage, 1)
                .set(JourneyTypeReviewPage, ChangeAnswers)

              implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), completedUserAnswers)
              JourneyTypeSection.isCompleted mustBe true
            }
        )
      }

      "when keep answers has been selected" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(),
            emptyUserAnswers
              .set(JourneyTypeReviewPage, KeepAnswers)
          )
        JourneyTypeSection.isCompleted mustBe true
      }
    }

    "must return false" - {
      "when in progress" in {
        val partiallyCompleteUserAnswers = emptyUserAnswers
          .set(HowMovementTransportedPage, Other)
          .set(JourneyTypeReviewPage, ChangeAnswers)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), partiallyCompleteUserAnswers,
          movementDetails = maxGetMovementResponse.copy(transportMode = maxGetMovementResponse.transportMode.copy(complementaryInformation = None)))
        JourneyTypeSection.isCompleted mustBe false
      }

      "when not finished" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.set(JourneyTypeReviewPage, ChangeAnswers))
        JourneyTypeSection.isCompleted mustBe false
      }

      "when not reviewed" in {
        val completedUserAnswers = emptyUserAnswers
          .set(HowMovementTransportedPage, Other)
          .set(GiveInformationOtherTransportPage, "information")
          .set(JourneyTimeDaysPage, 1)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), completedUserAnswers)
        JourneyTypeSection.isCompleted mustBe false
      }
    }
  }
}
