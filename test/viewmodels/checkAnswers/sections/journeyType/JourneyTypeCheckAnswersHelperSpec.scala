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

package viewmodels.checkAnswers.sections.journeyType

import base.SpecBase
import fixtures.messages.sections.journeyType._
import models.requests.DataRequest
import models.sections.journeyType.HowMovementTransported.AirTransport
import org.scalamock.scalatest.MockFactory
import pages.sections.journeyType._
import play.api.i18n.Messages
import play.api.test.FakeRequest
import viewmodels.helpers.CheckYourAnswersJourneyTypeHelper

class JourneyTypeCheckAnswersHelperSpec extends SpecBase with MockFactory {

  val days = 2
  val hours = 12

  trait Test {
    implicit val msgs: Messages = messages(Seq(CheckYourAnswersJourneyTypeMessages.English.lang))
    val helper = new CheckYourAnswersJourneyTypeHelper()
  }

  "summaryList" - {
    "must render three rows" - {
      s"when Journey Type has Hours of value is 12" in new Test {
        implicit val request: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(GiveInformationOtherTransportPage, "true")
            .set(HowMovementTransportedPage, AirTransport)
            .set(JourneyTimeHoursPage, hours)
        )
        helper.summaryList(false)(request, msgs).rows.length mustBe 3
      }
    }
    "must render three rows" - {
      s"when Journey Type Days value is 2" in new Test {
        implicit val request: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(GiveInformationOtherTransportPage, "true")
            .set(HowMovementTransportedPage, AirTransport)
            .set(JourneyTimeDaysPage, days),
          movementDetails = maxGetMovementResponse.copy(journeyTime = "4 days")
        )
        helper.summaryList(false)(request, msgs).rows.length mustBe 3
      }
    }
  }
}
