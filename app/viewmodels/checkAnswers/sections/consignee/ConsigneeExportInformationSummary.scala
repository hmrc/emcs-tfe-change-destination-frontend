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

import models.NormalMode
import models.requests.DataRequest
import pages.sections.consignee.ConsigneeExportInformationPage
import play.api.i18n.Messages
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.list

import javax.inject.Inject

class ConsigneeExportInformationSummary @Inject()(list: list) {

  def row(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {

    request.userAnswers.get(ConsigneeExportInformationPage).flatMap {
      identifications =>
        Some(
          summaryRow(
            list(
              identifications.map { provided =>
                Html(messages(s"consigneeExportInformation.checkYourAnswers.value.$provided"))
              }.toSeq
            )
          )
        )
    }
  }

  private def summaryRow(value: Html)(implicit request: DataRequest[_], messages: Messages): SummaryListRow = {
    SummaryListRowViewModel(
      key = "consigneeExportInformation.checkYourAnswers.label",
      value = ValueViewModel(HtmlContent(value)),
      actions = Seq(
        ActionItemViewModel(
          content = "site.change",
          href = controllers.sections.consignee.routes.ConsigneeExportInformationController.onPageLoad(
            ern = request.userAnswers.ern,
            arc = request.userAnswers.arc,
            mode = NormalMode
          ).url,
          id = "changeConsigneeExportInformation"
        )
          .withVisuallyHiddenText(messages("consigneeExportInformation.change.hidden"))
      )
    )
  }

}