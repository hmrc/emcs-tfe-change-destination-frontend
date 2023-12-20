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
import pages.sections.consignee.ConsigneeBusinessNamePage
import pages.sections.destination.{DestinationBusinessNamePage, DestinationConsigneeDetailsPage}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, SummaryListRow}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object DestinationBusinessNameSummary {

  def row()(implicit request: DataRequest[_], messages: Messages): SummaryListRow = {

    val useConsignee = request.userAnswers.get(DestinationConsigneeDetailsPage)

    val businessNamePage = useConsignee match {
      case Some(true) => ConsigneeBusinessNamePage
      case _ => DestinationBusinessNamePage
    }

    val changeBusinessNameLink = Seq(
      ActionItemViewModel(
        content = "site.change",
        href = controllers.sections.destination.routes.DestinationBusinessNameController.onPageLoad(request.ern, request.arc, CheckMode).url,
        id = "changeDestinationBusinessName"
      ).withVisuallyHiddenText(messages("destinationBusinessName.change.hidden"))
    )

    val (value, actions) = request.userAnswers.get(businessNamePage).fold[(String, Seq[ActionItem])] {
      useConsignee match {
        case Some(true) => (messages("destinationCheckAnswers.consignee.notProvided"), Seq.empty)
        case _ => (messages("destinationCheckAnswers.destination.notProvided"), changeBusinessNameLink)
      }
    } { answer =>
      useConsignee match {
        case Some(true) => (answer, Seq.empty)
        case _ => (answer, changeBusinessNameLink)
      }
    }

    SummaryListRowViewModel(
      key = "destinationBusinessName.checkYourAnswersLabel",
      value = ValueViewModel(HtmlFormat.escape(value).toString),
      actions = actions
    )
  }
}
