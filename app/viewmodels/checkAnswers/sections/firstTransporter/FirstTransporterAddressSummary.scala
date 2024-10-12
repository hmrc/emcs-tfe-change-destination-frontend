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

import models.CheckMode
import models.requests.DataRequest
import pages.sections.firstTransporter.FirstTransporterAddressPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Content, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object FirstTransporterAddressSummary {

  def row(onReviewPage: Boolean)(implicit request: DataRequest[_], messages: Messages): SummaryListRow = {

    val value: Content = request.userAnswers.get(FirstTransporterAddressPage).fold[Content]
      { Text(messages("site.notProvided")) }
      { _.toCheckYourAnswersFormat }

    SummaryListRowViewModel(
      key = "trader.firstTransporterAddress.checkYourAnswers.label",
      value = ValueViewModel(value),
      actions = if (onReviewPage) Seq() else Seq(
        ActionItemViewModel(
          content = "site.change",
          href = controllers.sections.firstTransporter.routes.FirstTransporterAddressController.onPageLoad(
            ern = request.userAnswers.ern,
            arc = request.userAnswers.arc,
            mode = CheckMode
          ).url,
          id = "changeFirstTransporterAddress"
        ).withVisuallyHiddenText(messages("trader.firstTransporterAddress.change.hidden"))
      )
    )
  }
}
