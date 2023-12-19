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

import models.NormalMode
import models.requests.DataRequest
import pages.sections.consignee.ConsigneeExportPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object ConsigneeExportSummary {

  def row(showActionLinks: Boolean)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    request.userAnswers.get(ConsigneeExportPage).map {
      answer =>

        val value = if (answer) "site.yes" else "site.no"

        SummaryListRowViewModel(
          key = "consigneeExport.checkYourAnswers.label",
          value = ValueViewModel(value),
          actions = if (!showActionLinks) Seq() else Seq(
            ActionItemViewModel(
              content = "site.change",
              href = controllers.sections.consignee.routes.ConsigneeExportController.onPageLoad(
                ern = request.userAnswers.ern,
                arc = request.userAnswers.arc,
                mode = NormalMode
              ).url,
              id = ConsigneeExportPage
            )
              .withVisuallyHiddenText(messages("consigneeExport.change.hidden"))
          )
        )
    }
}
