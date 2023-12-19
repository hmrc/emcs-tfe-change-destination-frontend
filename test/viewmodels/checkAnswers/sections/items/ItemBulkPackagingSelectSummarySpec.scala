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
import fixtures.messages.sections.items.ItemBulkPackagingSelectMessages
import models.CheckMode
import models.response.referenceData.BulkPackagingType
import models.sections.items.ItemBulkPackagingCode.BulkLiquid
import org.scalatest.matchers.must.Matchers
import pages.sections.items.ItemBulkPackagingSelectPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemBulkPackagingSelectSummarySpec extends SpecBase with Matchers with ItemFixtures {

  "ItemBulkPackagingSelectSummary" - {

    Seq(ItemBulkPackagingSelectMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output the expected data" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            ItemBulkPackagingSelectSummary.row(testIndex1) mustBe None
          }
        }

        "when there's an answer" - {

          "must output the expected row" in {
            implicit lazy val request = dataRequest(FakeRequest(),
              emptyUserAnswers.set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "Bulk, liquid"))
            )

            ItemBulkPackagingSelectSummary.row(testIndex1) mustBe Some(
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(HtmlContent("Bulk, liquid (VL)")),
                actions = Seq(
                  ActionItemViewModel(
                    content = messagesForLanguage.change,
                    href = controllers.sections.items.routes.ItemBulkPackagingSelectController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    id = "changeItemBulkPackagingSelect1"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                )
              )
            )
          }
        }
      }
    }
  }
}
