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

import models.requests.DataRequest
import pages.sections.movement.InvoiceDetailsPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.DateTimeUtils
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object InvoiceDetailsDateSummary extends DateTimeUtils {

  def row(onReviewPage: Boolean)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =

    request.userAnswers.get(InvoiceDetailsPage).map { answer =>

      SummaryListRowViewModel(
        key = messages(s"invoiceDetails.invoice-date.checkYourAnswersLabel"),
        value = ValueViewModel(answer.date.formatDateForUIOutput()),
        actions = if(onReviewPage) Seq() else Seq(
          ActionItemViewModel(
            "site.change",
            controllers.sections.movement.routes.InvoiceDetailsController.onPageLoad(request.ern, request.arc).url,
            id = "changeInvoiceDate"
          ).withVisuallyHiddenText(messages("invoiceDetails.invoice-date.change.hidden"))
      ))
    }
}
