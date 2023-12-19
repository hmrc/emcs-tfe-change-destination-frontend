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

import controllers.sections.documents.routes
import models.requests.DataRequest
import models.{CheckMode, Index}
import pages.sections.documents.{DocumentDescriptionPage, DocumentSection}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, SummaryListRow}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import viewmodels.taskList.Completed

object DocumentDescriptionSummary {

  def row(idx: Index)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    request.userAnswers.get(DocumentDescriptionPage(idx)).map {
      answer =>

        DocumentSection(idx).status match {
          case Completed =>
            SummaryListRowViewModel(
              key = "documentDescription.checkYourAnswersLabel",
              value = ValueViewModel(HtmlFormat.escape(answer).toString),
              actions = Seq(changeLink(idx))
            )
          case _ =>
            SummaryListRowViewModel(
              key = "documentDescription.checkYourAnswersLabel",
              value = ValueViewModel(HtmlFormat.escape(answer).toString)
            )
        }
    }

  private def changeLink(idx: Index)(implicit request: DataRequest[_], messages: Messages): ActionItem = {
    ActionItemViewModel(
      content = "site.change",
      href = routes.DocumentDescriptionController.onPageLoad(request.ern, request.arc, idx, CheckMode).url,
      id = s"changeDocumentDescription-${idx.displayIndex}"
    ).withVisuallyHiddenText(messages("documentDescription.change.hidden"))
  }
}
