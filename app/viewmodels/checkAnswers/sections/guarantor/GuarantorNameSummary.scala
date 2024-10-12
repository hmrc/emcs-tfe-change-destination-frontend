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
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

object GuarantorNameSummary {

  def row(onReviewPage: Boolean = false)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
//    request.userAnswers.get(GuarantorArrangerPage).flatMap { arranger =>
//      businessName(arranger).map { name =>
//        SummaryListRowViewModel(
//          key = "guarantorName.checkYourAnswersLabel",
//          value = ValueViewModel(name),
//          actions = if (!showChangeLink(arranger) || onReviewPage) {
//            Seq()
//          } else {
//            val mode = if (request.userAnswers.get(GuarantorNamePage).nonEmpty) CheckMode else NormalMode
//            Seq(
//              ActionItemViewModel(
//                "site.change",
//                controllers.sections.guarantor.routes.GuarantorNameController.onPageLoad(request.ern, request.arc, mode).url,
//                "changeGuarantorName"
//              ).withVisuallyHiddenText(messages("guarantorName.change.hidden"))
//            )
//          }
//        )
//      }
//    }
//
//  private def showChangeLink(arranger: GuarantorArranger): Boolean = arranger == GoodsOwner || arranger == Transporter
//
//  private def businessName(arranger: GuarantorArranger)(implicit request: DataRequest[_], messages: Messages): Option[String] = arranger match {
//    case Consignor => request.movementDetails.consignorTrader.traderName
//    case Consignee => Some(request.userAnswers.get(ConsigneeBusinessNamePage) match {
//      case Some(answer) => HtmlFormat.escape(answer).toString()
//      case None => messages("guarantorName.checkYourAnswers.notProvided", messages(s"guarantorArranger.$arranger"))
//    })
//    case _ =>
//      Some(request.userAnswers.get(GuarantorNamePage) match {
//        case Some(answer) => HtmlFormat.escape(answer).toString()
//        case None => messages("site.notProvided")
//      })
//  }

    // TODO
  None
}
