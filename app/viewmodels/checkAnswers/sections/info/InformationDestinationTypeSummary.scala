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

package viewmodels.checkAnswers.sections.info

import models.CheckMode
import models.requests.DataRequest
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object InformationDestinationTypeSummary {

  def row()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {

    request.userAnswers.get(DestinationTypePage).map { destinationType =>

      val value: String = messages(s"destinationType.$destinationType")

      SummaryListRowViewModel(
        key = "destinationType.checkYourAnswersLabel",
        value = ValueViewModel(value),
        actions = if (isOnPreDraftFlow) {
          Seq(
            ActionItemViewModel(
              "site.change",
              controllers.sections.info.routes.DestinationTypeController.onPreDraftPageLoad(ern = request.ern, CheckMode).url,
              id = "changeDestinationType")
              .withVisuallyHiddenText(messages("destinationType.change.hidden"))
          )
        } else {
          Seq()
        }
      )
    }
  }

}
