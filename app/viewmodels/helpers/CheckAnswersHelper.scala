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

import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.Aliases.{Actions, Key, SummaryListRow, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Content, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, SummaryList}
import viewmodels.govuk.summarylist._

import javax.inject.Inject

class CheckAnswersHelper @Inject()() {

  def summaryList(summaryListRows: Seq[SummaryListRow]): SummaryList =
    SummaryListViewModel(
      rows = summaryListRows
    ).withCssClass("govuk-!-margin-bottom-9")

  def buildSummaryRow(key: String,
                      value: Content,
                      optChangeLink: Option[Call] = None,
                      id: String = ""
                     )(implicit messages: Messages): SummaryListRow =
    SummaryListRow(
      key = Key(content = Text(messages(key))),
      value = Value(value),
      actions =
        optChangeLink match {
          case Some(changeLink) =>
            Some(Actions(
              items = Seq(ActionItem(
                href = changeLink.url,
                content = Text(messages("site.change")),
                visuallyHiddenText = Some(key),
                attributes = Map("id" -> id)
              ))
            ))
          case None => None
        }
    )
}
