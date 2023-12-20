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
import fixtures.messages.sections.info.LocalReferenceNumberMessages
import fixtures.messages.sections.info.LocalReferenceNumberMessages.ViewMessages
import models.CheckMode
import pages.sections.info.LocalReferenceNumberPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow, Value}
import viewmodels.govuk.summarylist._


class InformationLocalReferenceNumberSummarySpec extends SpecBase {

  private def expectedRow(value: String, deferredMovement: Boolean)(implicit messagesForLanguage: ViewMessages): Option[SummaryListRow] = {

    val cyaLabel: String = if (deferredMovement) messagesForLanguage.deferredCyaLabel else messagesForLanguage.newCyaLabel
    val cyaChangeHidden: String = if (deferredMovement) messagesForLanguage.deferredCyaChangeHidden else messagesForLanguage.newCyaChangeHidden

    Some(
      SummaryListRowViewModel(
        key = Key(Text(cyaLabel)),
        value = Value(Text(value)),
        actions = Seq(ActionItemViewModel(
          content = Text(messagesForLanguage.change),
          href = controllers.sections.info.routes.LocalReferenceNumberController.onPreDraftPageLoad(testErn, CheckMode).url,
          id = "changeLocalReferenceNumber"
        ).withVisuallyHiddenText(cyaChangeHidden))
      )
    )
  }

  Seq(LocalReferenceNumberMessages.English).foreach { implicit messagesForLanguage =>

    s"when language is set to ${messagesForLanguage.lang.code}" - {

      "and this is a deferred movement" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "and there is no answer for the LocalReferenceNumberPage" - {
          "then must not return a row" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            InformationLocalReferenceNumberSummary.row(deferredMovement = true) mustBe None
          }
        }

        "and there is a LocalReferenceNumberPage answer " - {
          "then must return a row with the answer" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(LocalReferenceNumberPage(), testLrn))

            InformationLocalReferenceNumberSummary.row(deferredMovement = true) mustBe expectedRow(value = testLrn, deferredMovement = true)
          }
        }

      }

      "and this is NOT a deferred movement" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "and there is no answer for the LocalReferenceNumberPage" - {
          "then must not return a row" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            InformationLocalReferenceNumberSummary.row(deferredMovement = false) mustBe None
          }
        }

        "and there is a LocalReferenceNumberPage answer " - {
          "then must return a row with the answer" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(LocalReferenceNumberPage(), testLrn))

            InformationLocalReferenceNumberSummary.row(deferredMovement = false) mustBe expectedRow(value = testLrn, deferredMovement = false)
          }
        }

      }
    }
  }

}
