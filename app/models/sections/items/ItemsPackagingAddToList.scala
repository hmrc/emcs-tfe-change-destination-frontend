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

package models.sections.items

import models.{Enumerable, WithName}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.ViewUtils.pluralSingular

sealed trait ItemsPackagingAddToList

object ItemsPackagingAddToList extends Enumerable.Implicits {

  case object Yes extends WithName("yes") with ItemsPackagingAddToList

  case object No extends WithName("no") with ItemsPackagingAddToList

  case object MoreLater extends WithName("moreLater") with ItemsPackagingAddToList

  val values: Seq[ItemsPackagingAddToList] = Seq(
    Yes, No, MoreLater
  )

  def options(totalPackages: Int, showNoOption: Boolean)(implicit messages: Messages): Seq[RadioItem] = {

    def radioItem(value: ItemsPackagingAddToList, index: Int, totalPackages: Option[Int]): RadioItem = RadioItem(
      content = totalPackages match {
        case None => Text(messages(s"itemsPackagingAddToList.${value.toString}"))
        case Some(amount) => Text(pluralSingular(s"itemsPackagingAddToList.${value.toString}", amount))
      },
      value = Some(value.toString),
      id = Some(s"value_$index")
    )

    val orDivider = RadioItem(
      divider = Some(messages(s"itemsPackagingAddToList.divider"))
    )

    Seq(
      Some(radioItem(Yes, 0, None)),
      if (showNoOption) Some(radioItem(No, 1, Some(totalPackages))) else None,
      if (showNoOption) Some(orDivider) else None,
      Some(radioItem(MoreLater, 2, None))
    ).flatten
  }

  implicit val enumerable: Enumerable[ItemsPackagingAddToList] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
