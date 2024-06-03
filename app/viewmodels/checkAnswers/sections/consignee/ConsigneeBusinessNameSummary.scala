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
import pages.sections.consignee.ConsigneeBusinessNamePage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._


object ConsigneeBusinessNameSummary {

  def row(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    request.userAnswers.get(ConsigneeBusinessNamePage).map {
      answer =>

        SummaryListRowViewModel(
          key = "consigneeBusinessName.checkYourAnswersLabel",
          value = ValueViewModel(HtmlFormat.escape(answer).toString),
          actions = Seq(
            ActionItemViewModel(
              content = "site.change",
              href = controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(request.ern, request.arc, CheckMode).url,
              id = "changeConsigneeBusinessName"
            ).withVisuallyHiddenText(messages("consigneeBusinessName.change.hidden"))
          )
        )
    }
}
