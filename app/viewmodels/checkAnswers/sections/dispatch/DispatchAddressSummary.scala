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

package viewmodels.checkAnswers.sections.dispatch

import models.CheckMode
import models.requests.DataRequest
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.dispatch.{DispatchAddressPage, DispatchUseConsignorDetailsPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._


object DispatchAddressSummary {

  def row()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    request.userAnswers.get(DispatchUseConsignorDetailsPage).flatMap {
      case true =>
        request.userAnswers.get(ConsignorAddressPage) match {
          case Some(answer) =>
            Some(SummaryListRowViewModel(
              key = "address.dispatchAddress.checkYourAnswersLabel",
              value = ValueViewModel(answer.toCheckYourAnswersFormat),
              actions = Seq()
            ))
          case None => Some(SummaryListRowViewModel(
            key = "address.dispatchAddress.checkYourAnswersLabel",
            value = ValueViewModel(Text(messages("address.dispatchAddress.checkYourAnswers.consignorNotComplete"))),
            actions = Seq()
          ))
        }
      case false =>
        request.userAnswers.get(DispatchAddressPage).map {
          answer =>
            SummaryListRowViewModel(
              key = "address.dispatchAddress.checkYourAnswersLabel",
              value = ValueViewModel(answer.toCheckYourAnswersFormat),
              actions = Seq(
                ActionItemViewModel(
                  content = "site.change",
                  href = controllers.sections.dispatch.routes.DispatchAddressController.onPageLoad(request.ern, request.arc, CheckMode).url,
                  id = "changeDispatchAddress"
                ).withVisuallyHiddenText(messages("address.dispatchAddress.change.hidden"))
              )
            )
        }
    }
  }
}
