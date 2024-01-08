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

package viewmodels.helpers

import base.SpecBase
import fixtures.messages.sections.transportUnit.TransportUnitTypeMessages
import models.UserAnswers
import models.sections.transportUnit.TransportUnitType.FixedTransport
import pages.sections.transportUnit.{TransportUnitIdentityPage, TransportUnitTypePage}
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.govuk.summarylist._

class CheckYourAnswersTransportUnitsHelperSpec extends SpecBase {

  class Setup(userAnswers: UserAnswers = emptyUserAnswers) {
    implicit lazy val request = dataRequest(FakeRequest(), userAnswers)

    lazy val helper: CheckYourAnswersTransportUnitsHelper = app.injector.instanceOf[CheckYourAnswersTransportUnitsHelper]
  }

  ".summaryList" - {
    "output an empty list when no transport unit type exists" in new Setup {
      helper.summaryList()(implicitly, messages(request)) mustBe SummaryList(
        rows = Seq.empty
      )
    }

    "output an empty list when no transport unit type exists (at the first index)" in new Setup(
      emptyUserAnswers
        .set(TransportUnitIdentityPage(testIndex1), "great success")
        .set(TransportUnitTypePage(testIndex2), FixedTransport)
    ) {
      helper.summaryList()(implicitly, messages(request)) mustBe SummaryList(
        rows = Seq.empty
      )
    }

    "output a list with the transport unit type when an answer exists at the first index" in new Setup(
      emptyUserAnswers.set(TransportUnitTypePage(testIndex1), FixedTransport)
    ) {
      helper.summaryList()(implicitly, messages(request)) mustBe SummaryList(
        rows = Seq(
          SummaryListRowViewModel(
            key = Key(Text(TransportUnitTypeMessages.English.cyaLabel)),
            value = ValueViewModel(Text(TransportUnitTypeMessages.English.addToListValue(FixedTransport))),
            actions = Seq.empty
          )
        )
      )
    }
  }

}
