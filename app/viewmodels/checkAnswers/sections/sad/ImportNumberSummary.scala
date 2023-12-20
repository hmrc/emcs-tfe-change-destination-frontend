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

package viewmodels.checkAnswers.sections.sad

import controllers.sections.sad.routes
import models.requests.DataRequest
import models.{CheckMode, Index}
import pages.sections.sad.ImportNumberPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Content
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object ImportNumberSummary {

  def row(idx: Index)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {

    Some(SummaryListRowViewModel(
      key = "importNumber.checkYourAnswersLabel",
      value = ValueViewModel(getValue(idx)),
      actions = {
        Seq(
          ActionItemViewModel(
            content = "site.change",
            routes.ImportNumberController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, idx, CheckMode).url,
            id = s"changeImportNumber${idx.displayIndex}"
          ).withVisuallyHiddenText(messages("importNumber.change.hidden"))
        )
      }
    ))
  }

  private def getValue(idx: Index)(implicit request: DataRequest[_], messages: Messages): Content =
    request.userAnswers.get(ImportNumberPage(idx)).fold(Text(messages("site.notProvided")))(answer => HtmlFormat.escape(answer).toString())

}
