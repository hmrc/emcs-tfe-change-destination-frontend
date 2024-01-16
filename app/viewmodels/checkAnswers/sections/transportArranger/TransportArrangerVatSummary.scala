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

package viewmodels.checkAnswers.sections.transportArranger

import models.CheckMode
import models.requests.DataRequest
import models.sections.transportArranger.TransportArranger.{GoodsOwner, Other}
import pages.sections.transportArranger.{TransportArrangerPage, TransportArrangerVatPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object TransportArrangerVatSummary {

  def row(onReviewPage: Boolean)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {

    request.userAnswers.get(TransportArrangerPage) match {
      case Some(GoodsOwner | Other) =>
        request.userAnswers.get(TransportArrangerVatPage).map { answer =>
          SummaryListRowViewModel(
            key = "transportArrangerVat.checkYourAnswers.label",
            value = ValueViewModel(answer),
            actions = if(onReviewPage) Seq.empty else Seq(
              ActionItemViewModel(
                content = "site.change",
                href = controllers.sections.transportArranger.routes.TransportArrangerVatController.onPageLoad(
                  ern = request.userAnswers.ern,
                  arc = request.userAnswers.arc,
                  mode = CheckMode
                ).url,
                id = "changeTransportArrangerVat"
              )
                .withVisuallyHiddenText(messages("transportArrangerVat.change.hidden"))
            )
          )
        }
      case _ => None
    }
  }
}
