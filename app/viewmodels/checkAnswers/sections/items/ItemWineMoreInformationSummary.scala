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

import controllers.sections.items.{routes => itemRoutes}
import models.requests.DataRequest
import models.{CheckMode, Index}
import pages.sections.items.ItemWineMoreInformationPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.JsonOptionFormatter
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.link

import javax.inject.Inject

class ItemWineMoreInformationSummary @Inject()(link: link) extends JsonOptionFormatter {

  def row(idx: Index)(implicit request: DataRequest[_], messages: Messages): SummaryListRow =
    request.userAnswers.get(ItemWineMoreInformationPage(idx)) match {
      case Some(Some(answer)) if answer != "" =>
        SummaryListRowViewModel(
          key = s"itemWineMoreInformation.checkYourAnswers.label",
          value = ValueViewModel(Text(HtmlFormat.escape(answer).toString())),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              itemRoutes.ItemWineMoreInformationController.onPageLoad(request.ern, request.arc, idx, CheckMode).url,
              id = s"changeItemWineMoreInformation${idx.displayIndex}"
            ).withVisuallyHiddenText(messages(s"itemWineMoreInformation.checkYourAnswers.change.hidden"))
          )
        )
      case _ =>
        SummaryListRowViewModel(
          key = s"itemWineMoreInformation.checkYourAnswers.label",
          value = ValueViewModel(HtmlContent(link(
            link = itemRoutes.ItemWineMoreInformationController.onPageLoad(request.ern, request.arc, idx, CheckMode).url,
            messageKey = s"itemWineMoreInformation.checkYourAnswers.addMoreInformation"
          )))
        )
    }
}
