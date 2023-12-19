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

package viewmodels.checkAnswers.sections.items

import controllers.sections.items.routes
import models.requests.DataRequest
import models.{CheckMode, Index}
import pages.sections.items.ItemBulkPackagingSelectPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object ItemBulkPackagingSelectSummary {

  def row(idx: Index)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    val answers = request.userAnswers
    answers.get(ItemBulkPackagingSelectPage(idx)).map {
      answer =>

        val value = ValueViewModel(
          HtmlContent(s"${answer.description} (${answer.packagingType.toString})")
        )

        SummaryListRowViewModel(
          key = "itemBulkPackagingSelect.checkYourAnswersLabel",
          value = value,
          actions = Seq(
            ActionItemViewModel(
              content = "site.change",
              href = routes.ItemBulkPackagingSelectController.onPageLoad(answers.ern, answers.arc, idx, CheckMode).url,
              id = s"changeItemBulkPackagingSelect${idx.displayIndex}"
            ).withVisuallyHiddenText(messages("itemBulkPackagingSelect.change.hidden"))
          )
        )
    }
  }
}
