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

import controllers.sections.items.routes
import models.requests.DataRequest
import models.{CheckMode, Index}
import pages.sections.items.{ItemPackagingSealTypePage, ItemsPackagingSectionItems}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

import javax.inject.Inject

class ItemPackagingSealInformationSummary @Inject()(link: views.html.components.link) {

  def row(itemIdx: Index, packagingIdx: Index)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    request.userAnswers.get(ItemPackagingSealTypePage(itemIdx, packagingIdx)).map(_.optSealInformation match {
      case Some(info) =>
        SummaryListRowViewModel(
          key = "itemPackagingSealType.sealInformation.checkYourAnswersLabel",
          value = ValueViewModel(info),
          actions = {
            if (!ItemsPackagingSectionItems(itemIdx, packagingIdx).isCompleted) Seq() else Seq(
              ActionItemViewModel(
                content = "site.change",
                routes.ItemPackagingSealTypeController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, itemIdx, packagingIdx, CheckMode).url,
                id = s"changeItemPackagingSealInformation${packagingIdx.displayIndex}ForItem${itemIdx.displayIndex}"
              ).withVisuallyHiddenText(messages("itemPackagingSealType.sealInformation.change.hidden"))
            )
          }
        )
      case None =>
        SummaryListRowViewModel(
          key = "itemPackagingSealType.sealInformation.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent(link(
            routes.ItemPackagingSealTypeController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, itemIdx, packagingIdx, CheckMode).url,
            messages("itemPackagingSealType.sealInformation.addMoreInfo")
          )))
        )
    })
}
