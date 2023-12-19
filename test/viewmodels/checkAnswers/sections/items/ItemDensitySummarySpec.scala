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
import fixtures.messages.sections.items.ItemDensityMessages
import models.requests.DataRequest
import models.{CheckMode, UserAnswers}
import pages.sections.items.ItemDensityPage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemDensitySummarySpec extends SpecBase {
  class Test(val userAnswers: UserAnswers) {
    implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)
  }

  "ItemDensitySummary" - {

    Seq(ItemDensityMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "if provided" - {
          "must return a row" in new Test(
            emptyUserAnswers
              .set(ItemDensityPage(testIndex1), BigDecimal(2.65))
          ) {
            ItemDensitySummary.row(
              idx = testIndex1
            ) mustBe
              Some(summaryListRowBuilder(
                key = HtmlContent(messagesForLanguage.cyaLabel),
                value = HtmlContent(s"2.65${messagesForLanguage.cyaSuffix}"),
                changeLink = Some(ActionItemViewModel(
                  href = controllers.sections.items.routes.ItemDensityController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  content = messagesForLanguage.change,
                  id = s"changeItemDensity${testIndex1.displayIndex}"
                ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
              ))
          }
        }
        "if not provided" - {
          "must not return a row" in new Test(emptyUserAnswers) {
            ItemDensitySummary.row(
              idx = testIndex1
            ) mustBe None
          }
        }
      }
    }
  }
}
