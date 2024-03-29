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
import fixtures.messages.sections.firstTransporter.FirstTransporterVatMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.firstTransporter.FirstTransporterVatPage
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._


class FirstTransporterVatSummarySpec extends SpecBase with Matchers {
  "FirstTransporterVatSummary" - {
    Seq(FirstTransporterVatMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

        "when the onReviewPage boolean is false" - {

          "when there is no answer in either 801 or user answers" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers, movementDetails = maxGetMovementResponse.copy(firstTransporterTrader = None))

            FirstTransporterVatSummary.row(onReviewPage = false) mustBe
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(Text(messagesForLanguage.notProvided)),
                actions = Seq(
                  ActionItemViewModel(
                    content = messagesForLanguage.change,
                    href = controllers.sections.firstTransporter.routes.FirstTransporterVatController.onPageLoad(testErn, testArc, CheckMode).url,
                    id = "changeFirstTransporterVat"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                )
              )
          }

          "when there's no answer in the user answers (defaulting to 801)" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            FirstTransporterVatSummary.row(onReviewPage = false) mustBe
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(Text("FirstTransporterTraderVatNumber")),
                actions = Seq(
                  ActionItemViewModel(
                    content = messagesForLanguage.change,
                    href = controllers.sections.firstTransporter.routes.FirstTransporterVatController.onPageLoad(testErn, testArc, CheckMode).url,
                    id = "changeFirstTransporterVat"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                )
              )
          }

          "when there is an answer" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(FirstTransporterVatPage, testVatNumber))

            FirstTransporterVatSummary.row(onReviewPage = false) mustBe SummaryListRowViewModel(
              key = messagesForLanguage.cyaLabel,
              value = Value(Text(testVatNumber)),
              actions = Seq(
                ActionItemViewModel(
                  content = messagesForLanguage.change,
                  href = controllers.sections.firstTransporter.routes.FirstTransporterVatController.onPageLoad(testErn, testArc, CheckMode).url,
                  id = "changeFirstTransporterVat"
                ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
              )
            )
          }
        }

        "when the onReviewPage boolean is true" - {

          "when there is no answer in either 801 or user answers" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers, movementDetails = maxGetMovementResponse.copy(firstTransporterTrader = None))

            FirstTransporterVatSummary.row(onReviewPage = true) mustBe
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(Text(messagesForLanguage.notProvided)),
                actions = Seq()
              )
          }

          "when there's no answer in the user answers (defaulting to 801)" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            FirstTransporterVatSummary.row(onReviewPage = true) mustBe SummaryListRowViewModel(
              key = messagesForLanguage.cyaLabel,
              value = Value(Text("FirstTransporterTraderVatNumber")),
              actions = Seq()
            )
          }

          "when there is an answer" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(FirstTransporterVatPage, testVatNumber))

            FirstTransporterVatSummary.row(onReviewPage = true) mustBe SummaryListRowViewModel(
              key = messagesForLanguage.cyaLabel,
              value = Value(Text(testVatNumber)),
              actions = Seq()
            )
          }
        }
      }
    }
  }
}
