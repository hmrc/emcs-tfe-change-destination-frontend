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

import models.CheckMode
import models.requests.DataRequest
import models.sections.guarantor.GuarantorArranger.{GoodsOwner, Transporter}
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorVatPage}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object GuarantorVatSummary {

  def row()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    request.userAnswers.get(GuarantorArrangerPage)
      .filter(arranger => arranger == GoodsOwner || arranger == Transporter)
      .map { _ =>

        val value: String = request.userAnswers.get(GuarantorVatPage) match {
          case Some(answer) => HtmlFormat.escape(answer).toString()
          case None => messages("site.notProvided")
        }

        SummaryListRowViewModel(
          key = "guarantorVat.checkYourAnswersLabel",
          value = ValueViewModel(value),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              controllers.sections.guarantor.routes.GuarantorVatController.onPageLoad(request.ern, request.arc, CheckMode).url,
              "changeGuarantorVat"
            ).withVisuallyHiddenText(messages("guarantorVat.change.hidden"))
          )
        )
      }
}
