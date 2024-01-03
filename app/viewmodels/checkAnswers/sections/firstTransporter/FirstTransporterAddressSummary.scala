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
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Content, HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object FirstTransporterAddressSummary {

  def row(showActionLinks: Boolean)(implicit request: DataRequest[_], messages: Messages): SummaryListRow = {

    val value: Content = request.userAnswers.get(FirstTransporterAddressPage).fold[Content] {
      Text(messages("site.notProvided"))
    } { address =>
      HtmlContent(
        HtmlFormat.fill(Seq(
          Html(address.property.fold("")(_ + " ") + address.street + "<br>"),
          Html(address.town + "<br>"),
          Html(address.postcode)
        ))
      )
    }

    SummaryListRowViewModel(
      key = "address.firstTransporterAddress.checkYourAnswers.label",
      value = ValueViewModel(value),
      actions = if (!showActionLinks) Seq() else Seq(
        ActionItemViewModel(
          content = "site.change",
          href = controllers.sections.firstTransporter.routes.FirstTransporterAddressController.onPageLoad(
            ern = request.userAnswers.ern,
            arc = request.userAnswers.arc,
            mode = CheckMode
          ).url,
          id = "changeFirstTransporterAddress"
        ).withVisuallyHiddenText(messages("address.firstTransporterAddress.change.hidden"))
      )
    )
  }
}
