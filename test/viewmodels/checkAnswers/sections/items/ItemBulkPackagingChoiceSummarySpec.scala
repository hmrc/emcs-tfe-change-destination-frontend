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
import fixtures.ItemFixtures
import fixtures.messages.sections.items.ItemBulkPackagingChoiceMessages
import models.{CheckMode, GoodsType}
import models.requests.DataRequest
import org.scalatest.matchers.must.Matchers
import pages.sections.items.ItemBulkPackagingChoicePage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Text, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemBulkPackagingChoiceSummarySpec extends SpecBase with Matchers with ItemFixtures {

  "ItemBulkPackagingChoiceSummary" - {

    Seq(ItemBulkPackagingChoiceMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output None" in {
            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

            ItemBulkPackagingChoiceSummary.row(testIndex1, GoodsType.Wine) mustBe None
          }
        }

        "when there's an answer" - {

          val sampleRow: SummaryListRow = SummaryListRowViewModel(
            key = messagesForLanguage.cyaLabel,
            value = Value(Text(messagesForLanguage.yes)),
            actions = Seq(
              ActionItemViewModel(
                content = messagesForLanguage.change,
                href = controllers.sections.items.routes.ItemBulkPackagingChoiceController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                id = "changeItemBulkPackagingChoice1"
              ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden("wine"))
            )
          )

          "must output the expected row" - {

            "when the answer is yes" in {

              implicit lazy val request: DataRequest[AnyContentAsEmpty.type] =
                dataRequest(FakeRequest(), emptyUserAnswers
                  .set(ItemBulkPackagingChoicePage(testIndex1), true)
                )

              ItemBulkPackagingChoiceSummary.row(testIndex1, GoodsType.Wine) mustBe Some(
                sampleRow.copy(value = Value(Text(messagesForLanguage.yes)))
              )
            }

            "when the answer is no" in {

              implicit lazy val request: DataRequest[AnyContentAsEmpty.type] =
                dataRequest(FakeRequest(), emptyUserAnswers
                  .set(ItemBulkPackagingChoicePage(testIndex1), false)
                )

              ItemBulkPackagingChoiceSummary.row(testIndex1, GoodsType.Wine) mustBe Some(
                sampleRow.copy(value = Value(Text(messagesForLanguage.no)))
              )
            }
          }
        }
      }
    }
  }
}
