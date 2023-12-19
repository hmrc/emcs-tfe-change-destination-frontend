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
import fixtures.messages.sections.items.ItemPackagingProductTypeMessages
import models.CheckMode
import models.requests.DataRequest
import org.scalatest.matchers.must.Matchers
import pages.sections.items.{ItemPackagingProductTypePage, ItemPackagingQuantityPage, ItemPackagingSealChoicePage, ItemSelectPackagingPage}
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Text, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Actions, SummaryListRow}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemPackagingProductTypeSummarySpec extends SpecBase with Matchers {

  "ItemPackagingProductTypeSummary" - {

    Seq(ItemPackagingProductTypeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output None" in {
            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

            ItemPackagingProductTypeSummary.row(testIndex1, testPackagingIndex1) mustBe None
          }
        }

        "when there's an answer" - {

          val rowNoChangeLink: SummaryListRow = SummaryListRowViewModel(
            key = messagesForLanguage.cyaLabel,
            value = Value(Text(messagesForLanguage.yes))
          )

          val rowWithChangeLink: SummaryListRow = rowNoChangeLink.copy(actions = Some(Actions(items =
            Seq(
              ActionItemViewModel(
                content = messagesForLanguage.change,
                href = controllers.sections.items.routes.ItemPackagingProductTypeController.onPageLoad(testErn, testDraftId, testIndex1,
                  testPackagingIndex1, CheckMode).url,
                id = "changePackagingProductType1ForItem1"
              ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
            ))
          ))

          "when the packaging item is complete" - {

            val userAnsers = emptyUserAnswers
              .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
              .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "5")
              .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)

            "must output the expected row" - {

              "when the answer is yes" in {

                implicit lazy val request: DataRequest[AnyContentAsEmpty.type] =
                  dataRequest(FakeRequest(), userAnsers
                    .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
                  )

                ItemPackagingProductTypeSummary.row(testIndex1, testPackagingIndex1) mustBe Some(
                  rowWithChangeLink.copy(value = Value(Text(messagesForLanguage.yes)))
                )
              }

              "when the answer is no" in {

                implicit lazy val request: DataRequest[AnyContentAsEmpty.type] =
                  dataRequest(FakeRequest(), userAnsers
                    .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), false)
                  )

                ItemPackagingProductTypeSummary.row(testIndex1, testPackagingIndex1) mustBe Some(
                  rowWithChangeLink.copy(value = Value(Text(messagesForLanguage.no)))
                )
              }
            }
          }

          "when the packaging item is NOT complete" - {

            val userAnsers = emptyUserAnswers
              .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)

            "must output the expected row (NO CHANGE LINK)" - {

              "when the answer is yes" in {

                implicit lazy val request: DataRequest[AnyContentAsEmpty.type] =
                  dataRequest(FakeRequest(), userAnsers
                    .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
                  )

                ItemPackagingProductTypeSummary.row(testIndex1, testPackagingIndex1) mustBe Some(
                  rowNoChangeLink.copy(value = Value(Text(messagesForLanguage.yes)))
                )
              }

              "when the answer is no" in {

                implicit lazy val request: DataRequest[AnyContentAsEmpty.type] =
                  dataRequest(FakeRequest(), userAnsers
                    .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), false)
                  )

                ItemPackagingProductTypeSummary.row(testIndex1, testPackagingIndex1) mustBe Some(
                  rowNoChangeLink.copy(value = Value(Text(messagesForLanguage.no)))
                )
              }
            }
          }
        }
      }
    }
  }
}
