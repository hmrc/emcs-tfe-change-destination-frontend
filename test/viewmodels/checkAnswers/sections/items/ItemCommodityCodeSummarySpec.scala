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
import fixtures.messages.sections.items.ItemCommodityCodeMessages
import models.requests.DataRequest
import models.{CheckMode, ExciseProductCode, UserAnswers}
import org.scalatest.matchers.must.Matchers
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.p

class ItemCommodityCodeSummarySpec extends SpecBase with Matchers with ItemFixtures {


  class Test(val userAnswers: UserAnswers) {
    lazy val messagesForLang: ItemCommodityCodeMessages.English.type = ItemCommodityCodeMessages.English
    lazy implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers, testErn)
    lazy implicit val msgs: Messages = messages(Seq(messagesForLang.lang))
    lazy val p: p = app.injector.instanceOf[p]
    lazy val itemCommodityCodeSummary: ItemCommodityCodeSummary = app.injector.instanceOf[ItemCommodityCodeSummary]
  }

  "ItemCommodityCodeSummary" - {

    "if EPC is not S500" - {
      "must return a row with no change link" - {
        ExciseProductCode.epcsOnlyOneCnCode.foreach(
          epc =>
            s"if EPC is $epc" in new Test(emptyUserAnswers) {
              itemCommodityCodeSummary.row(
                idx = testIndex1,
                cnCodeInformation = testCommodityCodeWine.copy(exciseProductCode = epc),
                mode = CheckMode
              ) mustBe
                Some(summaryListRowBuilder(
                  key = messagesForLang.cyaLabel,
                  value = HtmlContent(HtmlFormat.fill(Seq(
                    p()(Html(testCommodityCodeWine.cnCode)),
                    p()(Html(testCommodityCodeWine.cnCodeDescription))
                  ))),
                  changeLink = None
                ))
            }
        )
      }
      "must return a row with a change link" - {
        "if EPC has more than one CN Code" in new Test(emptyUserAnswers) {
          itemCommodityCodeSummary.row(
            idx = testIndex1,
            cnCodeInformation = testCommodityCodeWine,
            mode = CheckMode
          ) mustBe
            Some(summaryListRowBuilder(
              key = messagesForLang.cyaLabel,
              value = HtmlContent(HtmlFormat.fill(Seq(
                p()(Html(testCommodityCodeWine.cnCode)),
                p()(Html(testCommodityCodeWine.cnCodeDescription))
              ))),
              changeLink = Some(ActionItemViewModel(
                href = controllers.sections.items.routes.ItemCommodityCodeController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                content = messagesForLang.change,
                id = s"changeItemCommodityCode${testIndex1.displayIndex}"
              ).withVisuallyHiddenText(messagesForLang.cyaChangeHidden))
            ))
        }
      }
    }
    "if EPC is S500" - {
      "must not return a row" in new Test(emptyUserAnswers) {
        itemCommodityCodeSummary.row(
          idx = testIndex1,
          cnCodeInformation = testCommodityCodeWine.copy(exciseProductCode = "S500"),
          mode = CheckMode
        ) mustBe None
      }
    }
  }
}
