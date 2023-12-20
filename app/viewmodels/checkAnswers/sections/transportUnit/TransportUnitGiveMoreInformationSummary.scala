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

import models.requests.DataRequest
import models.{CheckMode, Index}
import pages.sections.transportUnit.TransportUnitGiveMoreInformationPage
import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.Aliases.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Content
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.JsonOptionFormatter.optionFormat
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object TransportUnitGiveMoreInformationSummary {

  def row(idx: Index)(implicit request: DataRequest[_], messages: Messages, link: views.html.components.link): Option[SummaryListRow] = {
    val optMoreInformation = request.userAnswers.get(TransportUnitGiveMoreInformationPage(idx)).flatten
    Some(SummaryListRowViewModel(
      key = "transportUnitGiveMoreInformation.checkYourAnswersLabel",
      value = ValueViewModel(getValue(optMoreInformation,
        controllers.sections.transportUnit.routes.TransportUnitGiveMoreInformationController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, idx, CheckMode))),
      actions = {
        Seq(
          optMoreInformation.map(_ =>
            ActionItemViewModel(
              content = "site.change",
              href = controllers.sections.transportUnit.routes.TransportUnitGiveMoreInformationController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, idx, CheckMode).url,
              id = s"changeTransportUnitMoreInformation${idx.displayIndex}"
            ).withVisuallyHiddenText(messages("transportUnitGiveMoreInformation.change.hidden"))
          )
        ).flatten
      }
    ))
  }

  private def getValue(optValue: Option[String], redirectUrl: Call)(implicit messages: Messages, link: views.html.components.link): Content =
    optValue.fold[Content](HtmlContent(link(redirectUrl.url, messages("transportUnitGiveMoreInformation.checkYourAnswersValue"))))(value => Text(value))

}
