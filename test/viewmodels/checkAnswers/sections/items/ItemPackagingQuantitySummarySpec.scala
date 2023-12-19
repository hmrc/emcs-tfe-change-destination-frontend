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
import controllers.sections.items.routes
import fixtures.messages.sections.items.ItemPackagingQuantityMessages
import models.CheckMode
import models.requests.DataRequest
import pages.sections.items._
import play.api.i18n.Messages
import play.api.test.FakeRequest
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemPackagingQuantitySummarySpec extends SpecBase {

  val messagesForLanguage: ItemPackagingQuantityMessages.English.type = ItemPackagingQuantityMessages.English
  implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

  "row" - {
    "must return a row with a link" - {
      "when ItemsPackagingSectionItems is completed" in {
        val userAnswers = emptyUserAnswers
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "4")
          .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        ItemPackagingQuantitySummary.row(testIndex1, testPackagingIndex1) mustBe Some(SummaryListRowViewModel(
          key = messagesForLanguage.cyaLabel,
          value = ValueViewModel("4"),
          actions = Seq(
            ActionItemViewModel(
              content = "site.change",
              routes.ItemPackagingQuantityController.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, CheckMode).url,
              id = s"changeItemPackagingQuantity${testPackagingIndex1.displayIndex}ForItem${testIndex1.displayIndex}"
            ).withVisuallyHiddenText(messagesForLanguage.cyaVisuallyHidden)
          )
        ))
      }
    }
    "must return a row without a link" - {
      "when ItemsPackagingSectionItems is not completed" in {
        val userAnswers = emptyUserAnswers
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "4")

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        ItemPackagingQuantitySummary.row(testIndex1, testPackagingIndex1) mustBe Some(SummaryListRowViewModel(
          key = messagesForLanguage.cyaLabel,
          value = ValueViewModel("4"),
          actions = Seq()
        ))
      }
    }
    "must return no row" - {
      "when ItemPackagingQuantityPage is not in user answers" in {
        val userAnswers = emptyUserAnswers
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        ItemPackagingQuantitySummary.row(testIndex1, testPackagingIndex1) mustBe None
      }
    }
  }
}
