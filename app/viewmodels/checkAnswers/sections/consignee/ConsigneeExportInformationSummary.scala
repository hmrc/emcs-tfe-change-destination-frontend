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

package viewmodels.checkAnswers.sections.consignee

import models.CheckMode
import models.requests.DataRequest
import models.sections.consignee.ConsigneeExportInformation
import models.sections.consignee.ConsigneeExportInformationType.{YesEoriNumber, YesVatNumber}
import pages.sections.consignee.ConsigneeExportInformationPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object ConsigneeExportInformationSummary {

  def row(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    request.userAnswers.get(ConsigneeExportInformationPage).flatMap {
      case ConsigneeExportInformation(YesVatNumber, Some(vat), _) => Some(summaryRow(YesVatNumber.toString, vat))
      case ConsigneeExportInformation(YesEoriNumber, _, Some(eori)) => Some(summaryRow(YesEoriNumber.toString, eori))
      case _ => Some(notKnownSummaryRow())
    }

  private def notKnownSummaryRow()(implicit request: DataRequest[_], messages: Messages): SummaryListRow =
    SummaryListRowViewModel(
      key = "consigneeExportInformation.checkYourAnswers.label.notKnown",
      value = ValueViewModel("consigneeExportInformation.checkYourAnswers.value.notKnown"),
      actions = Seq(
        ActionItemViewModel(
          content = "site.change",
          href = controllers.sections.consignee.routes.ConsigneeExportInformationController.onPageLoad(
            ern = request.userAnswers.ern,
            arc = request.userAnswers.arc,
            mode = CheckMode
          ).url,
          id = "changeConsigneeExportInformation"
        )
          .withVisuallyHiddenText(messages("consigneeExportInformation.change.hidden"))
      )
    )

  private def summaryRow(key: String, value: String)(implicit request: DataRequest[_], messages: Messages): SummaryListRow = {
    SummaryListRowViewModel(
      key = s"consigneeExportInformation.checkYourAnswers.label.$key",
      value = ValueViewModel(value),
      actions = Seq(
        ActionItemViewModel(
          content = "site.change",
          href = controllers.sections.consignee.routes.ConsigneeExportInformationController.onPageLoad(
            ern = request.userAnswers.ern,
            arc = request.userAnswers.arc,
            mode = CheckMode
          ).url,
          id = "changeConsigneeExportInformation"
        )
          .withVisuallyHiddenText(messages("consigneeExportInformation.change.hidden"))
      )
    )
  }

}
