/*
 * Copyright 2024 HM Revenue & Customs
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

package models.sections.consignee

import models.{Enumerable, WithName}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.{ExclusiveCheckbox, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem

sealed trait ConsigneeExportInformation

object ConsigneeExportInformation extends Enumerable.Implicits {

  case object VatNumber extends WithName("vatNumber") with ConsigneeExportInformation

  case object EoriNumber extends WithName("eoriNumber") with ConsigneeExportInformation

  case object NoInformation extends WithName("noInformation") with ConsigneeExportInformation

  val values: Seq[ConsigneeExportInformation] = Seq(
    VatNumber, EoriNumber, NoInformation
  )

  def options()(implicit messages: Messages): Seq[CheckboxItem] = {

    def checkBoxItem(index: Int, value: ConsigneeExportInformation, exclusive: Boolean = false): CheckboxItem = CheckboxItem(
      content = Text(messages(s"consigneeExportInformation.${value.toString}")),
      value = value.toString,
      id = Some(if (index == 0) "value" else s"value_$index"),
      behaviour = if (exclusive) Some(ExclusiveCheckbox) else None
    )

    val orDivider = CheckboxItem(
      divider = Some(messages("site.divider"))
    )

    Seq(
      Some(checkBoxItem(0, VatNumber)),
      Some(checkBoxItem(1, EoriNumber)),
      Some(orDivider),
      Some(checkBoxItem(2, NoInformation, exclusive = true))
    ).flatten
  }

  implicit val enumerable: Enumerable[ConsigneeExportInformation] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
