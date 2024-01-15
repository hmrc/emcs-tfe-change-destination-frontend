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

package viewmodels.checkAnswers.sections.movement

import base.SpecBase
import fixtures.messages.sections.movement.InvoiceDetailsMessages.English
import org.scalatest.matchers.must.Matchers
import pages.sections.movement.InvoiceDetailsPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import utils.DateTimeUtils
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

import java.time.LocalDate

class InvoiceDetailsDateSummarySpec extends SpecBase with Matchers with DateTimeUtils {

  "InvoiceDetailsDateSummary" - {

    s"when being rendered in lang code of '${English.lang.code}'" - {

      implicit val msgs: Messages = messages(Seq(English.lang))

      "when there's an answer" - {

        "must return expected output from UserAnswers" in {

          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(InvoiceDetailsPage, invoiceDetailsModel))

          InvoiceDetailsDateSummary.row() mustBe
            Some(
              SummaryListRowViewModel(
                key = English.cyaInvoiceDateLabel,
                value = Value(Text(invoiceDetailsModel.date.formatDateForUIOutput())),
                actions = Seq()
              )
            )
        }
      }

      "when there's no answer" - {

        "must return expected output from GetMovementResponse" in {

          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

          InvoiceDetailsDateSummary.row() mustBe Some(
            SummaryListRowViewModel(
              key = English.cyaInvoiceDateLabel,
              value = Value(Text(LocalDate.parse(testDateString).formatDateForUIOutput())),
              actions = Seq()
            )
          )
        }
      }
    }
  }
}
