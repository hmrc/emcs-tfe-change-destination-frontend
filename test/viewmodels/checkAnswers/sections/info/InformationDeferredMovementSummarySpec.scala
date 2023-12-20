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

package viewmodels.checkAnswers.sections.guarantor

import base.SpecBase
import fixtures.messages.sections.info.DeferredMovementMessages
import fixtures.messages.sections.info.DeferredMovementMessages.ViewMessages
import models.CheckMode
import pages.sections.info.DeferredMovementPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow, Value}
import viewmodels.checkAnswers.sections.info.InformationDeferredMovementSummary
import viewmodels.govuk.summarylist._


class InformationDeferredMovementSummarySpec extends SpecBase {
  private def expectedRow(value: String)(implicit messagesForLanguage: ViewMessages): Option[SummaryListRow] = {
    Some(
      SummaryListRowViewModel(
        key = Key(Text(messagesForLanguage.cyaLabel)),
        value = Value(Text(value)),
        actions = Seq(ActionItemViewModel(
          content = Text(messagesForLanguage.change),
          href = controllers.sections.info.routes.DeferredMovementController.onPreDraftPageLoad(testErn, CheckMode).url,
          id = "changeDeferredMovement"
        ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
      )
    )
  }

  Seq(DeferredMovementMessages.English).foreach { implicit messagesForLanguage =>

    s"when language is set to ${messagesForLanguage.lang.code}" - {

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      "and there is no answer for the DeferredMovementPage" - {
        "then must return a not provided row" in {
          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

          InformationDeferredMovementSummary.row mustBe expectedRow(value = messagesForLanguage.notProvided)
        }
      }

      "and there is a DeferredMovementPage answer of yes" - {
        "then must return a row with the answer of yes " in {
          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(DeferredMovementPage(), true))

          InformationDeferredMovementSummary.row mustBe expectedRow(value = messagesForLanguage.yes)
        }
      }

      "and there is a DeferredMovementPage answer of no" - {
        "then must return a row with the answer " in {
          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(DeferredMovementPage(), false))

          InformationDeferredMovementSummary.row mustBe expectedRow(value = messagesForLanguage.no)
        }
      }
    }
  }

}
