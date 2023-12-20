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
import models.response.referenceData.WineOperations
import models.{CheckMode, Index}
import pages.sections.items.ItemWineOperationsChoicePage
import play.api.i18n.Messages
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.list

import javax.inject.Inject

case class ItemWineOperationsChoiceSummary @Inject()(list: list) {

  def row(idx: Index)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    val page = ItemWineOperationsChoicePage(idx)

    request.userAnswers.get(page).map {
      answer =>

        val value = ValueViewModel(HtmlContent(
          if (answer.exists(_.code == WineOperations.nonWineOperationCode)) {
            Html(messages(s"$page.checkYourAnswersValue.none"))
          } else {
            list(answer.map { wineOperation =>
              Html(wineOperation.description)
            }.toSeq)
          }
        ))

        SummaryListRowViewModel(
          key = s"$page.checkYourAnswersLabel",
          value = value,
          actions = Seq(
            ActionItemViewModel(
              content = "site.change",
              href = routes.ItemWineOperationsChoiceController.onPageLoad(request.ern, request.arc, idx, CheckMode).url,
              id = s"changeWineOperationsChoice${idx.displayIndex}"
            ).withVisuallyHiddenText(messages(s"$page.change.hidden"))
          )
        )
    }
  }

}
