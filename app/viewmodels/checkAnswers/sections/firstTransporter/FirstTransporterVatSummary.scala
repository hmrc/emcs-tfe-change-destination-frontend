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
import pages.QuestionPage
import pages.sections.firstTransporter.FirstTransporterVatPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object FirstTransporterVatSummary {

  def row(onReviewPage: Boolean)(implicit request: DataRequest[_], messages: Messages): SummaryListRow = {

    val namePage: QuestionPage[String] = FirstTransporterVatPage

    SummaryListRowViewModel(
      key = "firstTransporterVat.checkYourAnswers.label",
      value = ValueViewModel(Text(request.userAnswers.get(namePage).getOrElse(messages("site.notProvided")))),
      actions = if (onReviewPage) Seq() else Seq(
        ActionItemViewModel(
          content = "site.change",
          href = controllers.sections.firstTransporter.routes.FirstTransporterVatController.onPageLoad(
            ern = request.userAnswers.ern,
            arc = request.userAnswers.arc,
            mode = CheckMode
          ).url,
          id = "changeFirstTransporterVat"
        )
          .withVisuallyHiddenText(messages("firstTransporterVat.change.hidden"))
      )
    )
  }
}
