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
import pages.sections.guarantor.{GuarantorRequiredPage, GuarantorSection}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object GuarantorRequiredSummary {

  def row(onReviewPage: Boolean)(implicit request: DataRequest[_], messages: Messages): SummaryListRow = {

    val value = request.userAnswers.get(GuarantorRequiredPage) match {
      case Some(answer) if !GuarantorSection.requiresGuarantorToBeProvided => if (answer) "site.yes" else "site.no"
      case _ => "site.yes"
    }

    SummaryListRowViewModel(
      key = "guarantorRequired.checkYourAnswersLabel",
      value = ValueViewModel(value),
      actions = if(onReviewPage || GuarantorSection.requiresGuarantorToBeProvided) Seq() else Seq(
        ActionItemViewModel(
          "site.change",
          controllers.sections.guarantor.routes.GuarantorRequiredController.onPageLoad(request.ern, request.arc, CheckMode).url,
          "changeGuarantorRequired"
        )
          .withVisuallyHiddenText(messages("guarantorRequired.change.hidden"))
      )
    )
  }

}
