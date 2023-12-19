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
import fixtures.messages.sections.items.ItemNetGrossMassMessages
import models.requests.DataRequest
import models.sections.items.ItemNetGrossMassModel
import models.{CheckMode, UserAnswers}
import org.scalatest.matchers.must.Matchers
import pages.sections.items.ItemNetGrossMassPage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemGrossMassSummarySpec extends SpecBase with Matchers {

  class Test(val userAnswers: UserAnswers) {
    implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)
  }

  "ItemGrossMassSummary" - {

    val messagesForLanguage = ItemNetGrossMassMessages.English

    s"when being rendered in lang code of 'en'" - {

      implicit lazy val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      "if provided" - {
        "must return a row" in new Test(
          emptyUserAnswers
            .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal(4.56), BigDecimal(7.89)))
        ) {
          ItemGrossMassSummary.row(
            idx = testIndex1,
          ) mustBe
            Some(summaryListRowBuilder(
              key = messagesForLanguage.cyaGrossMassLabel,
              value = s"7.89 ${messagesForLanguage.cyaSuffix}",
              changeLink = Some(ActionItemViewModel(
                href = controllers.sections.items.routes.ItemNetGrossMassController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                content = messagesForLanguage.change,
                id = s"changeItemGrossMass${testIndex1.displayIndex}"
              ).withVisuallyHiddenText(messagesForLanguage.cyaGrossMassChangeHidden))
            ))
        }
      }
      "if not provided" - {
        "must not return a row" in new Test(emptyUserAnswers) {
          ItemGrossMassSummary.row(
            idx = testIndex1
          ) mustBe None
        }
      }
    }
  }
}
