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

package viewmodels.checkAnswers.sections.firstTransporter

import base.SpecBase
import fixtures.messages.sections.firstTransporter.FirstTransporterNameMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.firstTransporter.FirstTransporterNamePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._


class FirstTransporterNameSummarySpec extends SpecBase with Matchers {
  "FirstTransporterNameSummary" - {
    val testBusinessName = "Some name"
    val testBusinessNameFrom801 = "FirstTransporterTraderName"

    Seq(FirstTransporterNameMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when the onReviewPage boolean is false" - {

          "when there is no answer in the user answers or in 801" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers, movementDetails = maxGetMovementResponse.copy(firstTransporterTrader = None))

            FirstTransporterNameSummary.row(onReviewPage = false) mustBe
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(Text(messagesForLanguage.notProvided)),
                actions = Seq(
                  ActionItemViewModel(
                    content = messagesForLanguage.change,
                    href = controllers.sections.firstTransporter.routes.FirstTransporterNameController.onPageLoad(testErn, testArc, CheckMode).url,
                    id = "changeFirstTransporterName"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                )
              )
          }

          "when there is no answer in the user answers (defaulting to 801)" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            FirstTransporterNameSummary.row(onReviewPage = false) mustBe
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(Text(testBusinessNameFrom801)),
                actions = Seq(
                  ActionItemViewModel(
                    content = messagesForLanguage.change,
                    href = controllers.sections.firstTransporter.routes.FirstTransporterNameController.onPageLoad(testErn, testArc, CheckMode).url,
                    id = "changeFirstTransporterName"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                )
              )
          }

          "when there is an answer" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(FirstTransporterNamePage, testBusinessName))

            FirstTransporterNameSummary.row(onReviewPage = false) mustBe SummaryListRowViewModel(
              key = messagesForLanguage.cyaLabel,
              value = Value(Text(testBusinessName)),
              actions = Seq(
                ActionItemViewModel(
                  content = messagesForLanguage.change,
                  href = controllers.sections.firstTransporter.routes.FirstTransporterNameController.onPageLoad(testErn, testArc, CheckMode).url,
                  id = "changeFirstTransporterName"
                ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
              )
            )
          }
        }

        "when the onReviewPage boolean is true" - {

          "when there is no answer in the user answers or 801" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers, movementDetails = maxGetMovementResponse.copy(firstTransporterTrader = None))

            FirstTransporterNameSummary.row(onReviewPage = true) mustBe
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(Text(messagesForLanguage.notProvided)),
                actions = Seq()
              )
          }

          "when there is no answer in the user answers (defaulting to 801)" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            FirstTransporterNameSummary.row(onReviewPage = true) mustBe
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(Text(testBusinessNameFrom801)),
                actions = Seq()
              )
          }

          "when there is an answer" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(FirstTransporterNamePage, testBusinessName))

            FirstTransporterNameSummary.row(onReviewPage = true) mustBe SummaryListRowViewModel(
              key = messagesForLanguage.cyaLabel,
              value = Value(Text(testBusinessName)),
              actions = Seq()
            )
          }
        }

      }
    }
  }
}
