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
import fixtures.messages.sections.items.ItemPackagingSealTypeMessages
import models.CheckMode
import models.requests.DataRequest
import models.sections.items.ItemPackagingSealTypeModel
import org.scalatest.matchers.must.Matchers
import pages.sections.items._
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Text, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Actions, SummaryListRow}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemPackagingSealTypeSummarySpec extends SpecBase with Matchers {

  "ItemPackagingSealTypeSummary" - {

    Seq(ItemPackagingSealTypeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output None" in {
            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

            ItemPackagingSealTypeSummary.row(testIndex1, testPackagingIndex1) mustBe None
          }
        }

        "when there's an answer" - {

          val rowNoChangeLink: SummaryListRow = SummaryListRowViewModel(
            key = messagesForLanguage.cyaLabelSealType,
            value = Value(Text("SEAL"))
          )

          val rowWithChangeLink: SummaryListRow = rowNoChangeLink.copy(
            actions = Some(Actions(items = Seq(
              ActionItemViewModel(
                content = messagesForLanguage.change,
                href = controllers.sections.items.routes.ItemPackagingSealTypeController.onPageLoad(testErn, testDraftId, testIndex1,
                  testPackagingIndex1, CheckMode).url,
                id = "changeItemPackagingSealType1ForItem1"
              ).withVisuallyHiddenText(messagesForLanguage.cyaSealTypeHiddenChange)
            )))
          )

          "when the packaging item is complete" - {

            val userAnswers = emptyUserAnswers
              .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
              .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "5")
              .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
              .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), true)
              .set(ItemPackagingSealTypePage(testIndex1, testPackagingIndex1), ItemPackagingSealTypeModel("SEAL", None))

            "must output the expected row" in {

              implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

              ItemPackagingSealTypeSummary.row(testIndex1, testPackagingIndex1) mustBe Some(rowWithChangeLink)
            }
          }

          "when the packaging item is NOT complete" - {

            val userAnswers = emptyUserAnswers
              .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
              .set(ItemPackagingSealTypePage(testIndex1, testPackagingIndex1), ItemPackagingSealTypeModel("SEAL", None))

            "must output the expected row (NO CHANGE LINK)" in {

              implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

              ItemPackagingSealTypeSummary.row(testIndex1, testPackagingIndex1) mustBe Some(rowNoChangeLink)
            }
          }
        }
      }
    }
  }
}
