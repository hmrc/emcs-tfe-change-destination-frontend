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

package viewmodels.checkAnswers.sections.consignee

import models.CheckMode
import models.requests.DataRequest
import models.sections.consignee.ConsigneeExportVat
import models.sections.consignee.ConsigneeExportVatType.{YesEoriNumber, YesVatNumber}
import pages.sections.consignee.ConsigneeExportVatPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object ConsigneeExportVatSummary {

  def row(showActionLinks: Boolean)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    request.userAnswers.get(ConsigneeExportVatPage).flatMap {
      case ConsigneeExportVat(YesVatNumber, Some(vat), _) => Some(summaryRow(showActionLinks, YesVatNumber.toString, vat))
      case ConsigneeExportVat(YesEoriNumber, _, Some(eori)) => Some(summaryRow(showActionLinks, YesEoriNumber.toString, eori))
      case _ => Some(notKnownSummaryRow())
    }

  private def notKnownSummaryRow()(implicit request: DataRequest[_], messages: Messages): SummaryListRow =
    SummaryListRowViewModel(
      key = "consigneeExportVat.checkYourAnswers.label.notKnown",
      value = ValueViewModel("consigneeExportVat.checkYourAnswers.value.notKnown"),
      actions = Seq(
        ActionItemViewModel(
          content = "site.change",
          href = controllers.sections.consignee.routes.ConsigneeExportVatController.onPageLoad(
            ern = request.userAnswers.ern,
            arc = request.userAnswers.arc,
            mode = CheckMode
          ).url,
          id = "changeConsigneeExportVat"
        )
          .withVisuallyHiddenText(messages("consigneeExportVat.change.hidden"))
      )
    )

  private def summaryRow(showActionLinks: Boolean, key: String, value: String)(implicit request: DataRequest[_], messages: Messages): SummaryListRow = {
    SummaryListRowViewModel(
      key = s"consigneeExportVat.checkYourAnswers.label.$key",
      value = ValueViewModel(value),
      actions = if (!showActionLinks) Seq() else Seq(
        ActionItemViewModel(
          content = "site.change",
          href = controllers.sections.consignee.routes.ConsigneeExportVatController.onPageLoad(
            ern = request.userAnswers.ern,
            arc = request.userAnswers.arc,
            mode = CheckMode
          ).url,
          id = "changeConsigneeExportVat"
        )
          .withVisuallyHiddenText(messages("consigneeExportVat.change.hidden"))
      )
    )
  }

}
