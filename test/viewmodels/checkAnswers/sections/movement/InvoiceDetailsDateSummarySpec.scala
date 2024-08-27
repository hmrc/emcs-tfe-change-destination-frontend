/*
 * Copyright 2024 HM Revenue & Customs
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

package viewmodels.checkAnswers.sections.movement

import base.SpecBase
import fixtures.messages.sections.movement.InvoiceDetailsMessages
import fixtures.messages.sections.movement.InvoiceDetailsMessages.English
import pages.sections.movement.InvoiceDetailsPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import utils.DateTimeUtils
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

import java.time.LocalDate

class InvoiceDetailsDateSummarySpec extends SpecBase with DateTimeUtils {

  "InvoiceDetailsDateSummary" - {

    Seq(InvoiceDetailsMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${English.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(English.lang))

        Seq(true, false).foreach { onReviewPage =>

          s"when onReviewPage = $onReviewPage" - {

            "when there's an answer" - {

              "must return expected output from UserAnswers" in {

                implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(InvoiceDetailsPage, invoiceDetailsModel))

                InvoiceDetailsDateSummary.row(onReviewPage) mustBe
                  Some(
                    SummaryListRowViewModel(
                      key = English.cyaInvoiceDateLabel,
                      value = Value(Text(invoiceDetailsModel.date.get.formatDateForUIOutput())),
                      actions = if (onReviewPage) Seq() else Seq(
                        ActionItemViewModel(
                          content = messagesForLanguage.change,
                          href = controllers.sections.movement.routes.InvoiceDetailsController.onPageLoad(testErn, testArc).url,
                          id = "changeInvoiceDate"
                        ).withVisuallyHiddenText(messagesForLanguage.cyaChangeInvoiceDateHidden)
                      )
                    )
                  )
              }
            }

            "when there's no answer" - {

              "must return expected output from GetMovementResponse" in {

                implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

                InvoiceDetailsDateSummary.row(onReviewPage) mustBe Some(
                  SummaryListRowViewModel(
                    key = English.cyaInvoiceDateLabel,
                    value = Value(Text(LocalDate.parse(testDateString).formatDateForUIOutput())),
                    actions = if (onReviewPage) Seq() else Seq(
                      ActionItemViewModel(
                        content = messagesForLanguage.change,
                        href = controllers.sections.movement.routes.InvoiceDetailsController.onPageLoad(testErn, testArc).url,
                        id = "changeInvoiceDate"
                      ).withVisuallyHiddenText(messagesForLanguage.cyaChangeInvoiceDateHidden)
                    )
                  )
                )
              }
            }
          }
        }
      }
    }
  }
}
