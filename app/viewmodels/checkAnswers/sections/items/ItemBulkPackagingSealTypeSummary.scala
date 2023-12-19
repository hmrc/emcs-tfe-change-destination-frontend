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
import models.{CheckMode, Index}
import pages.sections.items.ItemBulkPackagingSealTypePage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.link

class ItemBulkPackagingSealTypeSummary @Inject()(link: link) {

  def rows(idx: Index)(implicit request: DataRequest[_], messages: Messages): Seq[SummaryListRow] = {

    request.userAnswers.get(ItemBulkPackagingSealTypePage(idx)).map {
      value =>
        Seq(
          SummaryListRowViewModel(
            key = "itemPackagingSealType.sealType.checkYourAnswersLabel",
            value = ValueViewModel(value.sealType),
            actions = Seq(
              ActionItemViewModel(
                content = "site.change",
                routes.ItemBulkPackagingSealTypeController.onPageLoad(request.ern, request.arc, idx, CheckMode).url,
                id = s"changeItemBulkPackagingSealType${idx.displayIndex}"
              ).withVisuallyHiddenText(messages("itemPackagingSealType.sealType.change.hidden"))
            )
          ),
          value.optSealInformation match {
            case Some(value) => SummaryListRowViewModel(
              key = "itemPackagingSealType.sealInformation.checkYourAnswersLabel",
              value = ValueViewModel(value),
              actions = Seq(
                ActionItemViewModel(
                  content = "site.change",
                  routes.ItemBulkPackagingSealTypeController.onPageLoad(request.ern, request.arc, idx, CheckMode).url,
                  id = s"changeItemBulkPackagingSealInformation${idx.displayIndex}"
                ).withVisuallyHiddenText(messages("itemPackagingSealType.sealInformation.change.hidden"))
              )
            )
            case None => SummaryListRowViewModel(
              key = "itemPackagingSealType.sealInformation.checkYourAnswersLabel",
              value = ValueViewModel(HtmlContent(link(
                link = routes.ItemBulkPackagingSealTypeController.onPageLoad(request.ern, request.arc, idx, CheckMode).url,
                messageKey = "itemPackagingSealType.sealInformation.addMoreInfo"
              )))
            )
          }
        )
    }.getOrElse(Seq.empty)
  }
}
