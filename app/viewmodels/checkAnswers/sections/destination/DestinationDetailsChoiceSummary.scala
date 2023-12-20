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
import pages.sections.destination.DestinationDetailsChoicePage
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object DestinationDetailsChoiceSummary {

  def row()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    request.userAnswers.get(DestinationDetailsChoicePage).flatMap { answer =>
      request.userAnswers.get(DestinationTypePage).map { movementType =>

        val value: String = if (answer) "site.yes" else "site.no"

        SummaryListRowViewModel(
          key = messages(s"destinationDetailsChoice.checkYourAnswersLabel", movementType.stringValue),
          value = ValueViewModel(HtmlFormat.escape(value).toString),
          actions = Seq(
            ActionItemViewModel(
              content = "site.change",
              href = controllers.sections.destination.routes.DestinationDetailsChoiceController.onPageLoad(request.ern, request.arc, CheckMode).url,
              id = "changeDestinationDetailsChoice"
            ).withVisuallyHiddenText(messages("destinationDetailsChoice.change.hidden", movementType.stringValue))
          )
        )
      }
    }
}
