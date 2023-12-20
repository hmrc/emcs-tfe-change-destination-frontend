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
import pages.sections.consignee.ConsigneeAddressPage
import pages.sections.destination.{DestinationAddressPage, DestinationConsigneeDetailsPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, SummaryListRow, Value}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object DestinationAddressSummary {

  def row()(implicit request: DataRequest[_], messages: Messages): SummaryListRow = {

    val useConsignee = request.userAnswers.get(DestinationConsigneeDetailsPage)

    val businessNamePage = useConsignee match {
      case Some(true) => ConsigneeAddressPage
      case _ => DestinationAddressPage
    }

    val changeAddressLink = Seq(
      ActionItemViewModel(
        content = "site.change",
        href = controllers.sections.destination.routes.DestinationAddressController.onPageLoad(request.ern, request.arc, CheckMode).url,
        id = "changeDestinationAddress"
      ).withVisuallyHiddenText(messages("address.destinationAddress.change.hidden"))
    )

    val (value, actions) = request.userAnswers.get(businessNamePage).fold[(HtmlContent, Seq[ActionItem])] {
      useConsignee match {
        case Some(true) => (HtmlContent(messages("destinationCheckAnswers.consignee.notProvided")), Seq.empty)
        case _ => (HtmlContent(messages("destinationCheckAnswers.destination.notProvided")), Seq.empty)
      }
    } { answer =>
      useConsignee match {
        case Some(true) => (answer.toCheckYourAnswersFormat, Seq.empty)
        case _ => (answer.toCheckYourAnswersFormat, changeAddressLink)
      }
    }

    SummaryListRowViewModel(
      key = "address.destinationAddress.checkYourAnswers.label",
      value = Value(value),
      actions = actions
    )
  }
}
