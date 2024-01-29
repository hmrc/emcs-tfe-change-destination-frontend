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

package viewmodels.checkAnswers.sections.journeyType

import models.CheckMode
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementType
import models.sections.journeyType.HowMovementTransported.FixedTransportInstallations
import pages.sections.info.DestinationTypePage
import pages.sections.journeyType.HowMovementTransportedPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.Aliases.{HtmlContent, SummaryListRow}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object HowMovementTransportedSummary {

  def row(onReviewPage: Boolean)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    request.userAnswers.get(HowMovementTransportedPage).map {
      answer =>

        val value = ValueViewModel(
          HtmlContent(
            HtmlFormat.escape(messages(s"howMovementTransported.$answer"))
          )
        )

        SummaryListRowViewModel(
          key = "howMovementTransported.checkYourAnswers.label",
          value = value,
          actions = if (onReviewPage || hideChangeLink) Seq() else Seq(
            ActionItemViewModel(
              content = "site.change",
              href = controllers.sections.journeyType.routes.HowMovementTransportedController.onPageLoad(
                ern = request.userAnswers.ern,
                arc = request.userAnswers.arc,
                mode = CheckMode
              ).url,
              id = HowMovementTransportedPage
            ).withVisuallyHiddenText(messages("howMovementTransported.change.hidden"))
          )
        )
    }

  private def hideChangeLink(implicit request: DataRequest[_]): Boolean =
    request.userAnswers.get(DestinationTypePage).exists(_.movementType == MovementType.UkToEu) &&
      request.movementDetails.movementGuarantee.guarantorTrader.isEmpty && request.userAnswers.get(HowMovementTransportedPage).contains(FixedTransportInstallations)

}
