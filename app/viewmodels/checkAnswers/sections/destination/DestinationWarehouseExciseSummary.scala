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

package viewmodels.checkAnswers.sections.destination

import models.CheckMode
import models.requests.DataRequest
import pages.sections.destination.DestinationWarehouseExcisePage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object DestinationWarehouseExciseSummary {

  def row()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    request.userAnswers.get(DestinationWarehouseExcisePage).map {
      answer =>

        SummaryListRowViewModel(
          key = "destinationWarehouseExcise.checkYourAnswersLabel",
          value = ValueViewModel(HtmlFormat.escape(answer).toString),
          actions = Seq(
            ActionItemViewModel(
              content = "site.change",
              href = controllers.sections.destination.routes.DestinationWarehouseExciseController.onPageLoad(
                ern = request.ern,
                arc = request.arc,
                mode = CheckMode
              ).url,
              id = "changeDestinationWarehouseExcise")
              .withVisuallyHiddenText(messages("destinationWarehouseExcise.change.hidden"))
          )
        )
    }
}
