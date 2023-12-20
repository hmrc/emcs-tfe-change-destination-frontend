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
import fixtures.messages.sections.items.ItemPackagingShippingMarksMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.items._
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Text, Value}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemPackagingShippingMarksSummarySpec extends SpecBase with Matchers with ItemFixtures {

  "ItemPackagingShippingMarksSummary" - {

    Seq(ItemPackagingShippingMarksMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output None" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            ItemPackagingShippingMarksSummary.row(testIndex1, testPackagingIndex1) mustBe None
          }

        }

        "when there's an answer" - {

          "when the packaging item is complete" - {

            val userAnswers = emptyUserAnswers
              .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
              .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "5")
              .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), false)
              .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)

            "must output the expected row" in {

              implicit lazy val request = dataRequest(FakeRequest(), userAnswers
                .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "answer")
              )

              ItemPackagingShippingMarksSummary.row(testIndex1, testPackagingIndex1) mustBe Some(SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(Text("answer")),
                actions = Seq(
                  ActionItemViewModel(
                    content = messagesForLanguage.change,
                    href = controllers.sections.items.routes.ItemPackagingShippingMarksController.onPageLoad(testErn, testDraftId, testIndex1,
                      testPackagingIndex1, CheckMode).url,
                    id = "changeItemPackagingShippingMarks1ForItem1"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                )
              ))
            }
          }

          "when the packaging item is NOT complete" - {

            val userAnswers = emptyUserAnswers
              .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)

            "must output the expected row (NO CHANGE LINK)" in {

              implicit lazy val request = dataRequest(FakeRequest(), userAnswers
                .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "answer")
              )

              ItemPackagingShippingMarksSummary.row(testIndex1, testPackagingIndex1) mustBe Some(SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(Text("answer"))
              ))
            }
          }
        }
      }
    }
  }
}
