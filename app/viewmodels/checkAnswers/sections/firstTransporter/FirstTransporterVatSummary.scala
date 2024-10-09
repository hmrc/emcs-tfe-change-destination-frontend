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
import pages.sections.firstTransporter.FirstTransporterVatPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object FirstTransporterVatSummary {

  def row(onReviewPage: Boolean)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    FirstTransporterVatPage.value.flatMap {
      _.vatNumber.map {
        vatNumber =>
          SummaryListRowViewModel(
            key = "firstTransporterVat.vatNumber.checkYourAnswers.label",
            value = ValueViewModel(Text(vatNumber)),
            actions = if (onReviewPage) Seq() else Seq(
              ActionItemViewModel(
                content = "site.change",
                href = controllers.sections.firstTransporter.routes.FirstTransporterVatController.onPageLoad(
                  ern = request.userAnswers.ern,
                  arc = request.userAnswers.arc,
                  mode = CheckMode
                ).url,
                id = "changeFirstTransporterVatNumber"
              )
                .withVisuallyHiddenText(messages("firstTransporterVat.change.hidden"))
            )
          )
      }
    }
}
