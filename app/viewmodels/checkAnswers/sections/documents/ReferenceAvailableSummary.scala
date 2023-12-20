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

package viewmodels.checkAnswers.sections.documents

import models.requests.DataRequest
import models.{CheckMode, Index}
import pages.sections.documents.{DocumentSection, ReferenceAvailablePage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, SummaryListRow}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import viewmodels.taskList.Completed

object ReferenceAvailableSummary {

  def row(idx: Index)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    request.userAnswers.get(ReferenceAvailablePage(idx)).map {
      answer =>

        val value = if (answer) "site.yes" else "site.no"

        DocumentSection(idx).status match {
          case Completed =>
            SummaryListRowViewModel(
              key = "referenceAvailable.checkYourAnswersLabel",
              value = ValueViewModel(value),
              actions = Seq(changeLink(idx))
            )
          case _ =>
            SummaryListRowViewModel(
              key = "referenceAvailable.checkYourAnswersLabel",
              value = ValueViewModel(value)
            )
        }
    }

  private def changeLink(idx: Index)(implicit request: DataRequest[_], messages: Messages): ActionItem = {
    ActionItemViewModel(
      content = "site.change",
      href = controllers.sections.documents.routes.ReferenceAvailableController.onPageLoad(request.ern, request.arc, idx, CheckMode).url,
      id = s"changeReferenceAvailable-${idx.displayIndex}"
    ).withVisuallyHiddenText(messages("referenceAvailable.change.hidden"))
  }
}
