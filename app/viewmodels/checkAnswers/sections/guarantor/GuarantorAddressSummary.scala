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

package viewmodels.checkAnswers.sections.guarantor

import models.requests.DataRequest
import models.sections.guarantor.GuarantorArranger.{Consignee, Consignor, GoodsOwner, Transporter}
import models.{CheckMode, UserAddress}
import pages.QuestionPage
import pages.sections.consignee.ConsigneeAddressPage
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.guarantor.{GuarantorAddressPage, GuarantorArrangerPage}
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Content, HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object GuarantorAddressSummary {

  def row()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    request.userAnswers.get(GuarantorArrangerPage).map {

      guarantorArranger => {

        val addressPage: QuestionPage[UserAddress] = guarantorArranger match {
          case Consignor => ConsignorAddressPage
          case Consignee => ConsigneeAddressPage
          case _ => GuarantorAddressPage
        }

        val showChangeLink: Boolean = guarantorArranger == GoodsOwner || guarantorArranger == Transporter

        val value: Content = request.userAnswers.get(addressPage).fold[Content] {
          guarantorArranger match {
            case Consignor => Text(messages("address.guarantorAddress.checkYourAnswers.notProvided", messages(s"guarantorArranger.$Consignor")))
            case Consignee => Text(messages("address.guarantorAddress.checkYourAnswers.notProvided", messages(s"guarantorArranger.$Consignee")))
            case _ => Text(messages("site.notProvided"))
          }
        } { address =>
          HtmlContent(
            HtmlFormat.fill(Seq(
              Html(address.property.fold("")(_ + " ") + address.street + "<br>"),
              Html(address.town + "<br>"),
              Html(address.postcode)
            ))
          )
        }

        SummaryListRowViewModel(
          key = "address.guarantorAddress.checkYourAnswers.label",
          value = ValueViewModel(value),
          actions = if (!showChangeLink) Seq() else Seq(
            ActionItemViewModel(
              content = "site.change",
              href = controllers.sections.guarantor.routes.GuarantorAddressController.onPageLoad(
                ern = request.userAnswers.ern,
                arc = request.userAnswers.arc,
                mode = CheckMode
              ).url,
              id = "changeGuarantorAddress"
            )
              .withVisuallyHiddenText(messages("address.guarantorAddress.3.change.hidden"))
          )
        )
      }
    }
}
