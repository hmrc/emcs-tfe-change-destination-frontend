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

package viewmodels.checkAnswers.sections.transportUnit

import controllers.sections.transportUnit.routes
import models.requests.DataRequest
import models.sections.transportUnit.TransportSealTypeModel
import models.{CheckMode, Index}
import pages.sections.transportUnit.{TransportSealChoicePage, TransportSealTypePage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object TransportSealInformationSummary {

  def row(idx: Index, hideChangeLinks: Boolean)(implicit request: DataRequest[_], messages: Messages, link: views.html.components.link): Option[SummaryListRow] = {
    request.userAnswers.get(TransportSealChoicePage(idx)).filter(identity).map { _ =>
      request.userAnswers.get(TransportSealTypePage(idx)) match {
        case Some(TransportSealTypeModel(_, Some(info))) => SummaryListRowViewModel(
          key = "transportSealType.moreInfo.checkYourAnswersLabel",
          value = ValueViewModel(info),
          actions = if(hideChangeLinks) Seq() else Seq(
            ActionItemViewModel(
              "site.change",
              routes.TransportSealTypeController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, idx, CheckMode).url,
              s"changeTransportSealInformation${idx.displayIndex}"
            ).withVisuallyHiddenText(messages("transportSealType.moreInfo.change.hidden", idx.displayIndex))
          )
        )
        case _ =>
          val summaryRowValue = if(hideChangeLinks) Text(messages("site.notProvided")) else HtmlContent(link(
            link = routes.TransportSealTypeController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, idx, CheckMode).url,
            messageKey = s"transportSealType.moreInfo.checkYourAnswersAddInfo"))
          SummaryListRowViewModel(
          key = "transportSealType.moreInfo.checkYourAnswersLabel",
          value = ValueViewModel(summaryRowValue)
        )
      }
    }
  }

}
