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

package viewmodels.checkAnswers.sections.info

import base.SpecBase
import fixtures.messages.sections.info.DispatchDetailsMessages
import fixtures.messages.sections.info.DispatchDetailsMessages.ViewMessages
import models.CheckMode
import models.sections.info.DispatchDetailsModel
import pages.sections.info.DispatchDetailsPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow, Value}
import viewmodels.govuk.summarylist._

import java.time.{LocalDate, LocalTime}


class InformationDispatchTimeSummarySpec extends SpecBase {

  private def expectedRow(value: String)(implicit messagesForLanguage: ViewMessages): Option[SummaryListRow] = {
    Some(
      SummaryListRowViewModel(
        key = Key(Text(messagesForLanguage.cyaDispatchTimeLabel)),
        value = Value(Text(value)),
        actions = Seq(ActionItemViewModel(
          content = Text(messagesForLanguage.change),
          href = controllers.sections.info.routes.DispatchDetailsController.onPreDraftPageLoad(testErn, CheckMode).url,
          id = "changeTimeOfDispatch"
        ).withVisuallyHiddenText(messagesForLanguage.cyaChangeDispatchTimeHidden))
      )
    )
  }

  Seq(DispatchDetailsMessages.English).foreach { implicit messagesForLanguage =>

    s"when language is set to ${messagesForLanguage.lang.code}" - {

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      "and there is no answer for the DispatchDetailsPage" - {

        "then must return not provided row" in {
          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

          InformationTimeOfDispatchSummary.row mustBe expectedRow(messagesForLanguage.notProvided)
        }
      }

      "and there is a DispatchDetailsPage answer " - {

        "then must return a row with the answer" in {
          val model = DispatchDetailsModel(LocalDate.of(2023, 6, 7), LocalTime.of(7, 25))

          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(DispatchDetailsPage(), model))

          InformationTimeOfDispatchSummary.row mustBe expectedRow(value = "7:25")
        }
      }

    }
  }

}
