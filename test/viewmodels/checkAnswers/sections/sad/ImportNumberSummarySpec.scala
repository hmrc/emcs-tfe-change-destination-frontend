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

package viewmodels.checkAnswers.sections.sad

import base.SpecBase
import fixtures.messages.sections.sad.ImportNumberMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.sad.ImportNumberPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Text, Value}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ImportNumberSummarySpec extends SpecBase with Matchers {
  "ImportNumberSummary" - {

    Seq(ImportNumberMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output the expected data" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            ImportNumberSummary.row(testIndex1) mustBe Some(
              SummaryListRowViewModel(
                key = messagesForLanguage.checkYourAnswersLabel,
                value = Value(Text(messagesForLanguage.notProvided)),
                actions = Seq(
                  ActionItemViewModel(
                    content = messagesForLanguage.change,
                    href = controllers.sections.sad.routes.ImportNumberController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    id = "changeImportNumber1"
                  ).withVisuallyHiddenText(messagesForLanguage.changeHidden)
                )

              )
            )
          }
        }

        "when there's an answer" - {

          "must output the expected row" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(ImportNumberPage(testIndex1), "value"))

            ImportNumberSummary.row(testIndex1) mustBe Some(
              SummaryListRowViewModel(
                key = messagesForLanguage.checkYourAnswersLabel,
                value = Value(Text("value")),
                actions = Seq(
                  ActionItemViewModel(
                    content = messagesForLanguage.change,
                    href = controllers.sections.sad.routes.ImportNumberController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    id = "changeImportNumber1"
                  ).withVisuallyHiddenText(messagesForLanguage.changeHidden)
                )
              )
            )
          }
        }
      }
    }
  }
}
