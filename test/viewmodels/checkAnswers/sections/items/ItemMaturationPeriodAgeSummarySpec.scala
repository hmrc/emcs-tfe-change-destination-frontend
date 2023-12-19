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
import fixtures.messages.sections.items.ItemMaturationPeriodAgeMessages
import models.requests.DataRequest
import models.sections.items.ItemMaturationPeriodAgeModel
import models.{CheckMode, UserAnswers}
import org.scalatest.matchers.must.Matchers
import pages.sections.items.ItemMaturationPeriodAgePage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemMaturationPeriodAgeSummarySpec extends SpecBase with Matchers {

  class Test(val userAnswers: UserAnswers) {
    implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)
  }

  "ItemMaturationPeriodAgeSummary" - {

    Seq(ItemMaturationPeriodAgeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "if provided" - {
          "and hasMaturationPeriodAge is true" - {
            "must return a row with their answer if maturationPeriodAge is provided" in new Test(
              emptyUserAnswers
                .set(ItemMaturationPeriodAgePage(testIndex1), ItemMaturationPeriodAgeModel(
                  hasMaturationPeriodAge = true,
                  maturationPeriodAge = Some("test maturation period age")
                ))
            ) {
              ItemMaturationPeriodAgeSummary.row(idx = testIndex1) mustBe
                Some(summaryListRowBuilder(
                  key = messagesForLanguage.cyaLabel,
                  value = "test maturation period age",
                  changeLink = Some(ActionItemViewModel(
                    href = controllers.sections.items.routes.ItemMaturationPeriodAgeController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    content = messagesForLanguage.change,
                    id = s"changeItemMaturationPeriodAge${testIndex1.displayIndex}"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
                ))
            }
            "must return a row with default answer if maturationPeriodAge is not provided" in new Test(
              emptyUserAnswers
                .set(ItemMaturationPeriodAgePage(testIndex1), ItemMaturationPeriodAgeModel(
                  hasMaturationPeriodAge = true,
                  maturationPeriodAge = None
                ))
            ) {
              ItemMaturationPeriodAgeSummary.row(idx = testIndex1) mustBe
                Some(summaryListRowBuilder(
                  key = messagesForLanguage.cyaLabel,
                  value = messagesForLanguage.notProvided,
                  changeLink = Some(ActionItemViewModel(
                    href = controllers.sections.items.routes.ItemMaturationPeriodAgeController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    content = messagesForLanguage.change,
                    id = s"changeItemMaturationPeriodAge${testIndex1.displayIndex}"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
                ))
            }
          }
          "and hasMaturationPeriodAge is false" - {
            "must return a row with default answer even if maturationPeriodAge is provided" in new Test(
              emptyUserAnswers
                .set(ItemMaturationPeriodAgePage(testIndex1), ItemMaturationPeriodAgeModel(
                  hasMaturationPeriodAge = false,
                  maturationPeriodAge = Some("test maturation period age")
                ))
            ) {
              ItemMaturationPeriodAgeSummary.row(idx = testIndex1) mustBe
                Some(summaryListRowBuilder(
                  key = messagesForLanguage.cyaLabel,
                  value = messagesForLanguage.notProvided,
                  changeLink = Some(ActionItemViewModel(
                    href = controllers.sections.items.routes.ItemMaturationPeriodAgeController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    content = messagesForLanguage.change,
                    id = s"changeItemMaturationPeriodAge${testIndex1.displayIndex}"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
                ))
            }
            "must return a row with default answer if maturationPeriodAge is not provided" in new Test(
              emptyUserAnswers
                .set(ItemMaturationPeriodAgePage(testIndex1), ItemMaturationPeriodAgeModel(
                  hasMaturationPeriodAge = false,
                  maturationPeriodAge = None
                ))
            ) {
              ItemMaturationPeriodAgeSummary.row(idx = testIndex1) mustBe
                Some(summaryListRowBuilder(
                  key = messagesForLanguage.cyaLabel,
                  value = messagesForLanguage.notProvided,
                  changeLink = Some(ActionItemViewModel(
                    href = controllers.sections.items.routes.ItemMaturationPeriodAgeController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    content = messagesForLanguage.change,
                    id = s"changeItemMaturationPeriodAge${testIndex1.displayIndex}"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
                ))
            }
          }
        }
        "if not provided" - {
          "must not return a row" in new Test(emptyUserAnswers) {
            ItemMaturationPeriodAgeSummary.row(
              idx = testIndex1
            ) mustBe None
          }
        }
      }
    }
  }
}
