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

package viewmodels.helpers

import controllers.sections.transportUnit.{routes => transportUnitRoutes}
import models.Index
import models.requests.DataRequest
import play.api.i18n.Messages
import queries.TransportUnitsCount
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Actions, Card, CardTitle, SummaryList}
import viewmodels.checkAnswers.sections.transportUnit._
import viewmodels.govuk.summarylist._

import javax.inject.Inject

class TransportUnitsAddToListHelper @Inject()(implicit link: views.html.components.link) {

  def allTransportUnitsSummary()(implicit request: DataRequest[_], messages: Messages): Seq[SummaryList] = {
    request.userAnswers.get(TransportUnitsCount) match {
      case Some(value) => (0 until value).map(int => summaryList(Index(int)))
      case None => Nil
    }
  }

  private def summaryList(idx: Index)(implicit request: DataRequest[_], messages: Messages): SummaryList = {
    SummaryListViewModel(
      rows = Seq(
        TransportUnitTypeSummary.row(idx),
        TransportUnitIdentitySummary.row(idx),
        TransportSealChoiceSummary.row(idx),
        TransportSealTypeSummary.row(idx),
        TransportSealInformationSummary.row(idx),
        TransportUnitGiveMoreInformationSummary.row(idx)
      ).flatten
    ).copy(card = Some(Card(
      title = Some(CardTitle(Text(messages("transportUnitsAddToList.transportUnitCardTitle", idx.displayIndex)))),
      actions = Some(Actions(items = Seq(
        ActionItemViewModel(
          content = Text(messages("site.remove")),
          href = transportUnitRoutes.TransportUnitRemoveUnitController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, idx).url,
          id = s"removeTransportUnit${idx.displayIndex}"
        ).withVisuallyHiddenText(messages("transportUnitsAddToList.transportUnitCardTitle", idx.displayIndex))
      ))))
    ))
  }

}
