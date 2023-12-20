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
import fixtures.messages.sections.items.ItemSelectPackagingMessages
import models.CheckMode
import models.response.referenceData.ItemPackaging
import org.scalatest.matchers.must.Matchers
import pages.sections.items.{ItemPackagingProductTypePage, ItemPackagingQuantityPage, ItemPackagingSealChoicePage, ItemSelectPackagingPage}
import play.api.i18n.Messages
import play.api.test.FakeRequest
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemSelectPackagingSummarySpec extends SpecBase with Matchers {

  "ItemSelectPackagingSummary" - {

    Seq(ItemSelectPackagingMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output the expected data" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            ItemSelectPackagingSummary.row(testIndex1, testPackagingIndex1) mustBe None
          }
        }

        "when there's an answer" - {

          "when the package is complete" - {

            val userAnswers = emptyUserAnswers
              .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "5")
              .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), false)
              .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)

            "must output the expected row" in {

              implicit lazy val request = dataRequest(FakeRequest(),
                userAnswers.set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), ItemPackaging("AE", "Aerosol"))
              )

              ItemSelectPackagingSummary.row(testIndex1, testPackagingIndex1) mustBe Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = ValueViewModel("Aerosol"),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.items.routes.ItemSelectPackagingController.onPageLoad(testErn, testDraftId, testIndex1,
                        testPackagingIndex1, CheckMode).url,
                      id = "changeItemSelectPackaging1ForItem1"
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                )
              )
            }
          }

          "when the package is NOT complete" - {

            "must output the expected row (NO CHANGE LINK)" in {

              implicit lazy val request = dataRequest(FakeRequest(),
                emptyUserAnswers.set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), ItemPackaging("AE", "Aerosol"))
              )

              ItemSelectPackagingSummary.row(testIndex1, testPackagingIndex1) mustBe Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = ValueViewModel("Aerosol")
                )
              )
            }
          }
        }
      }
    }
  }
}
