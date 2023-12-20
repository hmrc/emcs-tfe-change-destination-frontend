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
import fixtures.messages.sections.items.ItemDegreesPlatoMessages
import models.requests.DataRequest
import models.sections.items.ItemDegreesPlatoModel
import models.{CheckMode, UserAnswers}
import org.scalatest.matchers.must.Matchers
import pages.sections.items.ItemDegreesPlatoPage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemDegreesPlatoSummarySpec extends SpecBase with Matchers {

  class Test(val userAnswers: UserAnswers) {
    implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)
  }

  "ItemDegreesPlatoSummarySummary" - {

    Seq(ItemDegreesPlatoMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "if provided" - {
          "and hasDegreesPlato is true" - {
            "must return a row with their answer if degreesPlato is provided" in new Test(
              emptyUserAnswers
                .set(ItemDegreesPlatoPage(testIndex1), ItemDegreesPlatoModel(hasDegreesPlato = true, degreesPlato = Some(BigDecimal(1.59))))
            ) {
              ItemDegreesPlatoSummary.row(idx = testIndex1) mustBe
                Some(summaryListRowBuilder(
                  key = messagesForLanguage.cyaLabel,
                  value = HtmlContent(s"1.59${messagesForLanguage.cyaSuffix}"),
                  changeLink = Some(ActionItemViewModel(
                    href = controllers.sections.items.routes.ItemDegreesPlatoController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    content = messagesForLanguage.change,
                    id = s"changeItemDegreesPlatoAmount${testIndex1.displayIndex}"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
                ))
            }
            "must return a row with default answer if degreesPlato is not provided" in new Test(
              emptyUserAnswers
                .set(ItemDegreesPlatoPage(testIndex1), ItemDegreesPlatoModel(hasDegreesPlato = true, degreesPlato = None))
            ) {
              ItemDegreesPlatoSummary.row(idx = testIndex1) mustBe
                Some(summaryListRowBuilder(
                  key = messagesForLanguage.cyaLabel,
                  value = HtmlContent(messagesForLanguage.no),
                  changeLink = Some(ActionItemViewModel(
                    href = controllers.sections.items.routes.ItemDegreesPlatoController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    content = messagesForLanguage.change,
                    id = s"changeItemDegreesPlatoAmount${testIndex1.displayIndex}"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
                ))
            }
          }
          "and hasDegreesPlato is false" - {
            "must return a row with default answer even if degreesPlato is provided" in new Test(
              emptyUserAnswers
                .set(ItemDegreesPlatoPage(testIndex1), ItemDegreesPlatoModel(hasDegreesPlato = false, degreesPlato = Some(BigDecimal(1.59))))
            ) {
              ItemDegreesPlatoSummary.row(idx = testIndex1) mustBe
                Some(summaryListRowBuilder(
                  key = messagesForLanguage.cyaLabel,
                  value = HtmlContent(messagesForLanguage.no),
                  changeLink = Some(ActionItemViewModel(
                    href = controllers.sections.items.routes.ItemDegreesPlatoController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    content = messagesForLanguage.change,
                    id = s"changeItemDegreesPlatoAmount${testIndex1.displayIndex}"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
                ))
            }
            "must return a row with default answer if degreesPlato is not provided" in new Test(
              emptyUserAnswers
                .set(ItemDegreesPlatoPage(testIndex1), ItemDegreesPlatoModel(hasDegreesPlato = false, degreesPlato = None))
            ) {
              ItemDegreesPlatoSummary.row(idx = testIndex1) mustBe
                Some(summaryListRowBuilder(
                  key = messagesForLanguage.cyaLabel,
                  value = HtmlContent(messagesForLanguage.no),
                  changeLink = Some(ActionItemViewModel(
                    href = controllers.sections.items.routes.ItemDegreesPlatoController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    content = messagesForLanguage.change,
                    id = s"changeItemDegreesPlatoAmount${testIndex1.displayIndex}"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
                ))
            }
          }
        }
        "if not provided" - {
          "must not return a row" in new Test(emptyUserAnswers) {
            ItemDegreesPlatoSummary.row(
              idx = testIndex1
            ) mustBe None
          }
        }
      }
    }
  }
}
