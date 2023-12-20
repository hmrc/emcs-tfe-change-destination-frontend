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

package viewmodels.checkAnswers.sections.items

import base.SpecBase
import fixtures.messages.sections.items.ItemImportedWineChoiceMessages
import models.CheckMode
import models.requests.DataRequest
import org.scalatest.matchers.must.Matchers
import pages.sections.items.ItemImportedWineFromEuChoicePage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemImportedWineChoiceSummarySpec extends SpecBase with Matchers {

  "ItemImportedWineChoiceSummary" - {

    val messagesForLanguage = ItemImportedWineChoiceMessages.English

    s"when being rendered in lang code of 'en'" - {

      implicit lazy val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      "when there's no answer" - {

        "must output None" in {

          implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

          ItemImportedWineChoiceSummary.row(testIndex1) mustBe None
        }
      }

      "when there's an answer" - {

        def expectedRow(msg: String): Option[SummaryListRow] =
          Some(SummaryListRowViewModel(
            key = messagesForLanguage.cyaLabel,
            value = Value(msg),
            actions = Seq(
              ActionItemViewModel(
                content = messagesForLanguage.change,
                href = controllers.sections.items.routes.ItemImportedWineChoiceController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                id = "changeItemImportedWineChoice1"
              ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
            )
          ))

        "answer is true" - {

          "must output the expected row" in {

            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] =
              dataRequest(FakeRequest(), emptyUserAnswers.set(ItemImportedWineFromEuChoicePage(testIndex1), true))

            ItemImportedWineChoiceSummary.row(testIndex1) mustBe expectedRow(messagesForLanguage.yes)
          }
        }

        "answer is false" - {

          "must output the expected row" in {

            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] =
              dataRequest(FakeRequest(), emptyUserAnswers.set(ItemImportedWineFromEuChoicePage(testIndex1), false))

            ItemImportedWineChoiceSummary.row(testIndex1) mustBe expectedRow(messagesForLanguage.no)
          }
        }
      }
    }
  }
}
