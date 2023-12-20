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

package viewmodels.checkAnswers.sections.items

import com.google.inject.Inject
import controllers.sections.items.routes
import models.requests.DataRequest
import models.response.referenceData.CnCodeInformation
import models.{ExciseProductCode, Index, Mode}
import pages.sections.items.ItemCommodityCodePage
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.p

class ItemCommodityCodeSummary @Inject()(p: p) {
  def row(idx: Index, cnCodeInformation: CnCodeInformation, mode: Mode)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {

    lazy val page = ItemCommodityCodePage(idx)

    if (ExciseProductCode.epcsWithNoCnCodes.contains(cnCodeInformation.exciseProductCode)) {
      None
    } else {
      Some(SummaryListRowViewModel(
        key = s"$page.checkYourAnswersLabel",
        value = ValueViewModel(HtmlContent(HtmlFormat.fill(Seq(
          p()(Html(cnCodeInformation.cnCode)),
          p()(Html(cnCodeInformation.cnCodeDescription))
        )))),
        actions = if (ExciseProductCode.epcsOnlyOneCnCode.contains(cnCodeInformation.exciseProductCode)) Seq() else Seq(ActionItemViewModel(
          href = routes.ItemCommodityCodeController.onPageLoad(request.ern, request.arc, idx, mode).url,
          content = "site.change",
          id = s"changeItemCommodityCode${idx.displayIndex}"
        ).withVisuallyHiddenText(messages(s"$page.change.hidden")))
      ))
    }
  }
}