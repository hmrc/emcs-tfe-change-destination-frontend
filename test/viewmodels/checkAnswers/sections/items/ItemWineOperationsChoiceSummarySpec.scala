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
import fixtures.messages.sections.items.ItemWineOperationsChoiceMessages
import models.CheckMode
import models.response.referenceData.WineOperations
import org.scalatest.matchers.must.Matchers
import pages.sections.items.ItemWineOperationsChoicePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.list

class ItemWineOperationsChoiceSummarySpec extends SpecBase with Matchers with ItemFixtures {

  "ItemWineOperationsChoiceSummary" - {

    lazy val list = app.injector.instanceOf[list]

    Seq(ItemWineOperationsChoiceMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must render None" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            ItemWineOperationsChoiceSummary(list).row(testIndex1) mustBe None
          }
        }

        "must output the expected row" - {
          "when there's an answer which isn't 'none'" in {

            implicit lazy val request = dataRequest(FakeRequest(),
              emptyUserAnswers.set(
                ItemWineOperationsChoicePage(testIndex1),
                Seq(
                  WineOperations("2", "The product has been acidified"),
                  WineOperations("3", "The product has been de-acidified")
                )
              )
            )

            ItemWineOperationsChoiceSummary(list).row(testIndex1) mustBe Some(
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = ValueViewModel(
                  HtmlContent(list(Seq(
                    Html("The product has been acidified"),
                    Html("The product has been de-acidified")
                  )))
                ),
                actions = Seq(
                  ActionItemViewModel(
                    content = messagesForLanguage.change,
                    href = controllers.sections.items.routes.ItemWineOperationsChoiceController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    id = "changeWineOperationsChoice1"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                )
              )
            )
          }

          "when there's an answer which contains 'none'" in {

            implicit lazy val request = dataRequest(FakeRequest(),
              emptyUserAnswers.set(
                ItemWineOperationsChoicePage(testIndex1),
                Seq(
                  WineOperations("2", "The product has been acidified"),
                  WineOperations("0", "unused")
                )
              )
            )

            ItemWineOperationsChoiceSummary(list).row(testIndex1) mustBe Some(
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = ValueViewModel(
                  HtmlContent(Html(messagesForLanguage.none))
                ),
                actions = Seq(
                  ActionItemViewModel(
                    content = messagesForLanguage.change,
                    href = controllers.sections.items.routes.ItemWineOperationsChoiceController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    id = "changeWineOperationsChoice1"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                )
              )
            )
          }

          "when the only answer is 'none'" in {
            implicit lazy val request = dataRequest(FakeRequest(),
              emptyUserAnswers.set(
                ItemWineOperationsChoicePage(testIndex1),
                Seq(
                  WineOperations("0", "unused")
                )
              )
            )

            ItemWineOperationsChoiceSummary(list).row(testIndex1) mustBe Some(
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = ValueViewModel(
                  HtmlContent(Html(messagesForLanguage.none))
                ),
                actions = Seq(
                  ActionItemViewModel(
                    content = messagesForLanguage.change,
                    href = controllers.sections.items.routes.ItemWineOperationsChoiceController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    id = "changeWineOperationsChoice1"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                )
              )
            )
          }
        }
      }
    }
  }
}
